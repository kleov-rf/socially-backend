# Socially Backend

Spring Boot modular monolith for the Socially platform. HTTP APIs cover donations (including images and geo sorting), Google OAuth session flows, and the authenticated user profile. Persistence uses PostgreSQL, Flyway migrations, and optional S3-backed donation media.

## Current Scope

### Donation

- Create, partial update, soft delete, get by id, and cursor-paginated list.
- Required **location** on create (`address`, `latitude`, `longitude`); optional location on patch.
- List filters: text `query`; sort orders `newest_first`, `oldest_first`, **`nearest_first`** (requires `latitude` and `longitude`).
- **Images:** presigned S3 `PUT` upload, CDN `mediaUrl` on get-by-id, delete image (DB row + S3 object).
- Soft delete via `deleted_at` on donations.

### Auth

- `GET /api/auth/login/google` — redirect to Cognito Hosted UI / OAuth authorize.
- `GET /api/auth/callback/google` — exchange code, set httpOnly refresh cookie, redirect to frontend.
- `POST /api/auth/refresh` — refresh session from cookie (public).
- `POST /api/auth/logout` — revoke refresh token (authenticated).

### User

- `GET /api/users/me` — current user profile (authenticated JWT).

### Donor

- Internal bounded context (no dedicated public HTTP controllers in `app`).
- Donor records linked to federated users and donations; orchestrated from donation and auth flows via `infrastructure:right` adapters.

### Not implemented in this repository today

- Public OpenAPI/Swagger generation.
- Background jobs, message consumers, or schedulers beyond request/response handling.
- Additional product domains beyond donation, auth, user, and donor support described above.

## Deployment Dependencies

AWS deployments assume bootstrap and infrastructure are applied first. Local development substitutes MiniStack for Cognito, RDS, and S3 (see [Local development](#local-development)).

| Layer | Repository | What the backend needs |
|-------|------------|-------------------------|
| Bootstrap | [`socially-terraform-bootstrap`](../socially-terraform-bootstrap/README.md) | SSM `/config/socially/backend/container/image-version`; Secrets Manager `DD_API_KEY`; auth secret containers |
| Infrastructure | [`socially-infrastructure`](../socially-infrastructure/README.md) | ECS service, ECR, RDS, Cognito outputs, donation media S3 + CloudFront `/media` base URL |

**Apply order:** bootstrap → infrastructure → backend image deploy (this repo CI).

**ECS / `dev` profile environment (from infrastructure Terraform):**

- Database: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- Cognito JWT: `COGNITO_ISSUER_URL`, `COGNITO_USER_POOL_ID`, `COGNITO_REGION`, `COGNITO_HOSTED_DOMAIN`, `COGNITO_SPA_CLIENT_ID`, `COGNITO_BACKEND_CLIENT_ID`, `COGNITO_BACKEND_CLIENT_SECRET_JSON`
- OAuth: `AUTH_REDIRECT_URI` (SPA callback when CloudFront is enabled)
- Media: `MEDIA_STORAGE_S3_BUCKET`, `MEDIA_STORAGE_CDN_BASE_URL` (bound to `media.storage` in `application-dev.yaml`)
- Profile: `SPRING_PROFILES_ACTIVE` = `dev` | `stg` | `prd`

CI uses bootstrap OIDC roles `github-agent-ecr` and `github-agent-ecs` for image push and ECS deploy.

## Architecture

Hexagonal (ports and adapters) per vertical slice:

- **domain** — entities, value objects, domain rules.
- **application** — use cases (`*CommandHandler`, `*QueryHandler`).
- **infrastructure:left** — HTTP controllers, request/response DTOs.
- **infrastructure:right** — JPA, S3, Cognito, and other outbound adapters.

**`app`** assembles Spring Boot and wires left adapters from each bounded context. Full module list: `settings.gradle.kts`.

```text
app
├── auth (login, callback, refresh, logout, kernel)
├── user (me, create, federated-identity, update-profile, kernel)
├── donor (create, find-by-id, find-by-user-id, kernel)
├── donation (create, find, get-by-id, update, delete, create-image, delete-image, kernel)
└── commons (observability)
```

Typical slice naming: `donation:create:application`, `donation:create:infrastructure:left`, etc.

## API Surface

Base URL: `http://localhost:8080/api` (local). Health: `http://localhost:8080/actuator/health`.

### Donations

| Method | Path | Auth (see [Security](#security)) | Notes |
|--------|------|----------------------------------|-------|
| `POST` | `/donations` | JWT | Body: `id`, `title`, `description`, `location` (`address`, `latitude`, `longitude`). `201`. |
| `GET` | `/donations` | Permit all | Query: `cursor`, `size` (`5`/`10`/`20`), `order` (`newest_first`, `oldest_first`, `nearest_first`), `query`, `latitude`, `longitude` (required for `nearest_first`). `200` + `items` and `page` metadata. |
| `GET` | `/donations/{id}` | Permit all | `200` with `location`, `donor`, `images[]` (`imageId`, `mediaUrl`, `contentType`, `sizeBytes`, `primary`); `404` if missing or soft-deleted. |
| `PATCH` | `/donations/{id}` | JWT | Partial `title`, `description`, `location`. `204` or `404`. |
| `DELETE` | `/donations/{id}` | JWT | Soft delete. `204`. |

**List response page metadata:** `nextCursor`, `previousCursor`, `hasNext`, `hasPrevious`, `size`, `totalCount`.

### Donation images

| Method | Path | Auth | Notes |
|--------|------|------|-------|
| `POST` | `/donations/{donationId}/images` | Permit all | Body: `originalFileName`, `contentType`, `sizeBytes`, `primary`. `201` + `imageId`, `uploadUrl`, `mediaUrl`, etc. Client must **HTTP PUT** file bytes to `uploadUrl`. |
| `DELETE` | `/donations/{donationId}/images/{imageId}` | Permit all | Removes DB row and S3 object. `204`. |

### Auth

| Method | Path | Auth | Notes |
|--------|------|------|-------|
| `GET` | `/auth/login/google` | Public | Redirect to OAuth / Hosted UI. |
| `GET` | `/auth/callback/google` | Public | Query: `code`, `state`. Sets refresh cookie; redirects to app. |
| `POST` | `/auth/refresh` | Public | Uses httpOnly refresh cookie. |
| `POST` | `/auth/logout` | JWT | Revokes session. |

### Users

| Method | Path | Auth | Notes |
|--------|------|------|-------|
| `GET` | `/users/me` | JWT | Current user profile. |

## Security

Configured in `auth/kernel/infrastructure/left/SecurityConfiguration`. CSRF, form login, HTTP basic, and framework logout are disabled. OAuth2 resource server JWT is enabled when `spring.security.oauth2.resourceserver.jwt.issuer-uri` is set.

| Path / method | Requirement |
|---------------|-------------|
| `GET /actuator/health` | Public |
| `GET /api/auth/login/google` | Public |
| `GET /api/auth/callback/google` | Public |
| `POST /api/auth/refresh` | Public |
| `POST /api/donations` | **Authenticated** (JWT) |
| `PATCH /api/donations/*` | **Authenticated** |
| `DELETE /api/donations/*` | **Authenticated** |
| `GET /api/users/me` | **Authenticated** |
| `POST /api/auth/logout` | **Authenticated** |
| All other routes (`GET /api/donations`, `GET /api/donations/{id}`, image routes, etc.) | **Permit all** (no JWT required today) |

Integration tests use mock JWT where scenarios require an authenticated principal (for example image upload ownership).

**Profiles:**

- **`local`** — MiniStack issuer/JWKS; see [Local AWS emulation (MiniStack)](#local-aws-emulation-ministack).
- **`dev` / `stg` / `prd`** — real Cognito issuer from `COGNITO_ISSUER_URL`.

## Data and Runtime

- **Framework:** Spring Boot WebMVC, Validation, Actuator, Spring Data JPA, OAuth2 resource server.
- **Database:** PostgreSQL (`DB_*` env vars).
- **Migrations:** Flyway under `app/src/main/resources/db/migration` (including users, federated identities, donors, donation location, `donation_images`).
- **Media:** `media.storage` (`s3`, `cdn.base-url`, `presign.duration`) — local overrides via `LOCAL_MEDIA_STORAGE_*` in `application-local.yaml`; AWS via `MEDIA_STORAGE_*` in `application-dev.yaml`.
- **Production image:** Datadog Java agent (`dd-java-agent.jar`).

## Local Development

### Prerequisites

- Java 25
- Docker (Compose, MiniStack RDS, Testcontainers)

### Option 1: Run with Docker Compose

From this repository:

```bash
docker compose up --build
```

Services started:

- Backend: `http://localhost:8080`

### Local AWS emulation (MiniStack)

Local development uses MiniStack instead of the AWS ECS stack described in [Deployment dependencies](#deployment-dependencies). MiniStack (`ministackorg/ministack`) provides **Cognito**, **RDS PostgreSQL**, and **S3-compatible storage** (presigned donation image uploads) via API + Secrets Manager semantics without real AWS credentials. MiniStack requires access to the host **Docker socket** for RDS containers.

Run:

```bash
./scripts/start-local.sh
```

What this does:

- starts MiniStack (`http://localhost:4566`)
- runs `scripts/ministack-init.sh` inside MiniStack to create/reuse:
  - Cognito user pool `socially-local`, SPA/backend clients, and `socially/cognito/backend-client-secret`
  - RDS instance `socially-local` (PostgreSQL **18.2**, ephemeral / `RDS_PERSIST=0`)
  - Secrets Manager `socially/db/credentials` (JSON: `host`, `port`, `dbname`, `username`, `password`)
  - S3 bucket **`socially-media`** (bucket name from **`LOCAL_MEDIA_STORAGE_S3_BUCKET`**, default **`socially-media`**) used for presigned `PUT` uploads
  - writes **`LOCAL_COGNITO_*`** to `/tmp/ministack/cognito-outputs.env` and **`LOCAL_DB_*`** to `/tmp/ministack/db-outputs.env`
- exports those vars and starts `backend` (JDBC targets `host.docker.internal:15432` from inside Compose)

**Database (RDS):** default host port **`15432`** (`RDS_BASE_PORT`). Credentials: user `socially_admin`, password `LocalDevPass1!` (override with `LOCAL_RDS_MASTER_PASSWORD`). Data is **ephemeral** — recreating the ministack container yields an empty database; Flyway re-runs on backend start.

Recreate MiniStack after init script changes: `docker compose up -d --force-recreate ministack`, then `./scripts/start-local.sh` again.

If browser uploads fail with **404** on `PUT` to `http://localhost:4566/...`, the media bucket may be missing (e.g. init not re-run). Create it manually:

```bash
aws --endpoint-url http://localhost:4566 --region us-east-1 s3 mb s3://socially-media
```

(Default local credentials: **`test`** / **`test`**, same as **`AWS_ACCESS_KEY_ID`** / **`AWS_SECRET_ACCESS_KEY`** in `ministack-init.sh`.)

**S3 in Docker Compose:** the backend container uses two endpoints (same pattern as Cognito `LOCAL_COGNITO_OAUTH_BASE_URL` vs `LOCAL_COGNITO_HOSTED_DOMAIN`): **`LOCAL_MEDIA_STORAGE_S3_ENDPOINT_URL=http://ministack:4566`** for server SDK calls (e.g. delete object), and **`LOCAL_MEDIA_STORAGE_S3_PUBLIC_ENDPOINT_URL=http://localhost:4566`** for presigned browser `PUT` URLs. **`./gradlew bootRun` on the host** keeps a single default `http://localhost:4566` for both. Recreate the backend container after changing these env vars.

MiniStack does not register hosted-UI identity providers named `Google` or `COGNITO`. The **`local`** profile uses **`LOCAL_COGNITO_IDENTITY_PROVIDER`** (default empty) so authorize URLs omit `identity_provider`. For real Cognito, `application-dev.yaml` uses `Google`. Override with **`LOCAL_COGNITO_IDENTITY_PROVIDER`** if needed. Recreate the backend container after changing env.

JWTs from MiniStack use an AWS-style `iss` claim while JWKS is loaded via `http://ministack:4566/...`. The emulator `JwtDecoder` is registered when **`auth.oauth.use-ministack=true`**, driven by **`LOCAL_COGNITO_USE_MINISTACK`** (defaults true in `application-local.yaml`). **`bootRun`** sets **`LOCAL_COGNITO_USE_MINISTACK=true`** when **`COGNITO_USE_MINISTACK=true`** is exported.

The Cognito token endpoint exposed via MiniStack accepts `grant_type=refresh_token` with the SPA app client id (no secret), matching AWS semantics and backing `POST /api/auth/refresh` for reloading the SPA with an existing httpOnly refresh cookie.

**Hosted UI username/password:** `dev@socially.local` / `SociallyDev1!` (created by `scripts/ministack-init.sh`). The seed user also gets profile attributes `given_name=Dev` and `family_name=User`. Override via ministack env `LOCAL_DEV_COGNITO_USERNAME` / `LOCAL_DEV_COGNITO_PASSWORD` / `LOCAL_DEV_COGNITO_GIVEN_NAME` / `LOCAL_DEV_COGNITO_FAMILY_NAME`. If your MiniStack volume predates that logic, run `docker compose up -d --force-recreate ministack`, wait for Cognito init, then `./scripts/start-local.sh` again.

Quick checks:

```bash
curl -f http://localhost:8080/actuator/health
docker compose exec -T backend sh -lc 'wget -qO- "$LOCAL_COGNITO_ISSUER_URL/.well-known/jwks.json" | head -c 200'
```

### Option 2: Run from Gradle/IDE

Start MiniStack (`./scripts/start-local.sh` once, or `docker compose up -d ministack` and wait for init), then source outputs on the **host**:

```bash
docker compose exec -T ministack cat /tmp/ministack/cognito-outputs.env /tmp/ministack/db-outputs.env > /tmp/socially-local.env
set -a && source /tmp/socially-local.env && set +a
export LOCAL_DB_HOST=localhost
export LOCAL_COGNITO_HOSTED_DOMAIN=http://localhost:4566
export LOCAL_COGNITO_ISSUER_URL="http://localhost:4566/${LOCAL_COGNITO_USER_POOL_ID}"
./gradlew :app:bootRun
```

Use **`LOCAL_DB_HOST=localhost`** (not `host.docker.internal`) when the JVM runs on the host. Port comes from `db-outputs.env` (default **15432**).

The `bootRun` task defaults `spring.profiles.active` to **`local`**, seeds **`LOCAL_DB_*`** defaults, and can mirror **`LOCAL_COGNITO_USE_MINISTACK`** from **`COGNITO_USE_MINISTACK`**. To match **AWS ECS** (real Cognito + RDS), use the deployment profile (`dev`, `stg`, or `prd`) — not the `local` machine profile:

```bash
SPRING_PROFILES_ACTIVE=dev ./gradlew :app:bootRun
```

Use this only when your shell provides the same Cognito-related variables as the target ECS task (see [Deployment dependencies](#deployment-dependencies)). For everyday workstation + MiniStack, keep **`local`** and run **`./scripts/start-local.sh`** (or export the same **`LOCAL_COGNITO_*`** variables **`start-local`** sets before `bootRun`).

In an IDE, set **VM options** `-Dspring.profiles.active=local` or the environment variable **`SPRING_PROFILES_ACTIVE=local`** on your run configuration when developing on your machine.

`application.yaml` does not default a profile. **AWS:** Terraform sets `SPRING_PROFILES_ACTIVE` on ECS to `dev`, `stg`, or `prd`. **Machine:** use `local` (`docker-compose.yml` sets `SPRING_PROFILES_ACTIVE: local`).

### Build and format

```bash
./gradlew build
./gradlew spotlessCheck
./gradlew spotlessApply
```

## Testing

### Unit and module tests

```bash
./gradlew test
```

Notes:

- Uses JUnit Platform.
- Excludes Cucumber runner from the default `test` task.
- Generates JaCoCo XML/HTML reports for modules with tests.

### Integration/BDD tests

```bash
./gradlew testIntegration
```

Notes:

- Runs Cucumber scenarios against PostgreSQL via Testcontainers (and LocalStack for S3 in image scenarios).
- Includes `@auth`, `@donation`, and donation-image tagged scenarios in a single run.
- Uses the **`test`** Spring profile; overlays from `app/src/test/resources/application.yaml`, not `local`.
- Requires Docker on the host.

## CI/CD

Main workflow: [`.github/workflows/cicd.yaml`](.github/workflows/cicd.yaml)

- **Triggers:** push to `main`; `workflow_dispatch` with `environment` input (currently `dev`).
- **Permissions:** `id-token: write` for OIDC AWS authentication.

**Pipeline:**

1. **Pre-checks** (parallel): Snyk (`_snyk-scan.yaml`), Sonar (`_sonar-scan.yaml`), integration tests (`_tests.yaml` — `testIntegration`).
2. **Build** (`_build-image.yaml`) — JAR artifact for Docker image.
3. **Push** (`_push-image.yaml`) — build and push image to ECR (`github-agent-ecr`).
4. **Deploy** (`_deploy-image.yaml`) — new ECS task definition, deploy service, update SSM image tag (`github-agent-ecs`).

**Deployment contracts:**

- AWS region: `eu-south-2`
- ECS cluster/service: `socially-<env>-cluster`, `socially-<env>-service`
- SSM parameter on deploy: `/config/socially/backend/container/image-version`
- Task environment aligns with [Deployment dependencies](#deployment-dependencies) (Cognito, DB, media CDN, `AUTH_REDIRECT_URI` when infra enables CloudFront/auth).

## Repository Layout

```text
.
├── app/
├── commons/
├── auth/
├── user/
├── donor/
├── donation/
│   ├── kernel/
│   ├── create/
│   ├── create-image/
│   ├── delete-image/
│   ├── delete/
│   ├── get-by-id/
│   ├── update/
│   └── find/
├── scripts/
│   ├── start-local.sh
│   └── ministack-init.sh
├── .github/workflows/
├── Dockerfile
├── Dockerfile.dev
├── docker-compose.yml
├── build.gradle.kts
└── settings.gradle.kts
```

## Troubleshooting

- **Plan/boot fails on image version SSM:** apply [bootstrap](../socially-terraform-bootstrap/README.md) first.
- **Presigned image PUT returns 404:** MiniStack bucket missing — see [Local AWS emulation (MiniStack)](#local-aws-emulation-ministack).
- **List with `nearest_first` fails:** both `latitude` and `longitude` are required for that order.
- **Auth / JWT validation errors locally:** verify issuer/JWKS quick checks above; confirm `LOCAL_COGNITO_USE_MINISTACK` / `auth.oauth.use-ministack` for MiniStack tokens.
- **Auth against real AWS (`dev` profile on host):** ensure `COGNITO_ISSUER_URL` and secrets match [infrastructure](../socially-infrastructure/README.md); Google OAuth secret name must be `socially-dev/auth/google-oauth` (not variable default `socially/google-oauth`).
- **ECS missing DB/Redis env:** infrastructure feature flags — see infra README.
- **CI deploy skipped or failed:** check OIDC `AWS_ROLE_ARN` and bootstrap `github-agent-ecr` / `github-agent-ecs` roles.
