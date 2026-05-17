# Socially Backend

Spring Boot backend for Socially. The currently implemented bounded context is donation management, exposed through `/api/donations` with create, list, get-by-id, partial update, and delete operations.

## Current Scope

Implemented in this repository today:

- Donation write flows: create, partial update, delete.
- Donation read flows: get by id and list.
- Cursor pagination, configurable page size, sort order, and text query filtering on list endpoint.
- Soft delete behavior (`deleted_at`) enforced across read and update operations.
- Flyway-managed PostgreSQL schema evolution.
- CI pipeline for security scan, code quality scan, image build, ECR push, and ECS deploy.

Not implemented in this repository today:

- Additional business domains beyond donations.
- Background jobs/message consumers/schedulers.
- Public OpenAPI/Swagger contract generation.

## Architecture

The codebase is a modular monolith using hexagonal layering:

- `domain` modules: entities, value objects, core rules.
- `application` modules: use cases and orchestration.
- `infrastructure:left` modules: HTTP adapters/controllers.
- `infrastructure:right` modules: persistence adapters.
- `app` module: Spring Boot runtime assembly.

Main module graph:

```text
app
├── commons:observability
├── donation:create:infrastructure:left
├── donation:delete:infrastructure:left
├── donation:get-by-id:infrastructure:left
├── donation:update:infrastructure:left
└── donation:find:infrastructure:left
```

All declared Gradle modules are listed in `settings.gradle.kts`.

## API Surface

Base URL: `http://localhost:8080/api`

### Endpoints

- `POST /donations` -> creates a donation (`201`).
- `GET /donations` -> lists donations (`200`) with optional query params:
  - `cursor`
  - `size` (`5`, `10`, `20`)
  - `order` (`newest_first`, `oldest_first`)
  - `query` (text filter)
- `GET /donations/{id}` -> returns donation or `404`.
- `PATCH /donations/{id}` -> partial update (title and/or description), returns `204` or `404`.
- `DELETE /donations/{id}` -> delete operation, returns `204`.

### Request/response notes

- Create request requires `id`, `title`, and `description`.
- List response includes `items` plus `page` metadata:
  - `nextCursor`, `previousCursor`, `hasNext`, `hasPrevious`, `size`, `totalCount`.
- Delete is implemented as soft delete (`deleted_at`), validated by BDD scenarios.

## Data and Runtime

- Runtime framework: Spring Boot WebMVC + Validation + Actuator + Spring Data JPA.
- Database: PostgreSQL (configured via `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`).
- Migrations: Flyway SQL scripts under `app/src/main/resources/db/migration`.
- Health endpoint: `/actuator/health` (exposed through management config).
- Production container includes Datadog Java agent (`dd-java-agent.jar`).

## Local Development

### Prerequisites

- Java 25
- Docker (required for local compose and integration tests)

### Option 1: Run with Docker Compose

From this repository:

```bash
docker compose up --build
```

Services started:

- Backend: `http://localhost:8080`

### Local AWS emulation (MiniStack)

This repository uses MiniStack (`ministackorg/ministack`) for local **Cognito** and **RDS PostgreSQL**, matching production-style provisioning (API + Secrets Manager) without real AWS credentials. MiniStack requires access to the host **Docker socket** to run RDS database containers.

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
  - writes **`LOCAL_COGNITO_*`** to `/tmp/ministack/cognito-outputs.env` and **`LOCAL_DB_*`** to `/tmp/ministack/db-outputs.env`
- exports those vars and starts `backend` (JDBC targets `host.docker.internal:15432` from inside Compose)

**Database (RDS):** default host port **`15432`** (`RDS_BASE_PORT`). Credentials: user `socially_admin`, password `LocalDevPass1!` (override with `LOCAL_RDS_MASTER_PASSWORD`). Data is **ephemeral** — recreating the ministack container yields an empty database; Flyway re-runs on backend start.

Recreate MiniStack after init script changes: `docker compose up -d --force-recreate ministack`, then `./scripts/start-local.sh` again.

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

Use this only when your shell provides the same Cognito-related variables as the target ECS task. For everyday workstation + MiniStack, keep **`local`** and run **`./scripts/start-local.sh`** (or export the same **`LOCAL_COGNITO_*`** variables **`start-local`** sets before `bootRun`).

In an IDE, set **VM options** `-Dspring.profiles.active=local` or the environment variable **`SPRING_PROFILES_ACTIVE=local`** on your run configuration when developing on your machine.

`application.yaml` does not default a profile. **AWS:** Terraform sets `SPRING_PROFILES_ACTIVE` on ECS to `dev`, `stg`, or `prd`. **Machine:** use `local` (e.g. `docker-compose.yml` now sets `SPRING_PROFILES_ACTIVE: local`).

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

- Runs Cucumber scenarios against PostgreSQL via Testcontainers.
- Includes both `@auth` and non-auth integration scenarios in a single run.
- Uses the **`test`** Spring profile by default; test-only config overlays from `app/src/test/resources/application.yaml`, not `local`.
- Requires Docker available on the host.

## CI/CD

Main workflow: `.github/workflows/cicd.yaml`

On push to `main` (or manual dispatch for `dev`) the pipeline runs:

1. Snyk scan (`_snyk-scan.yaml`)
2. Sonar scan (`_sonar-scan.yaml`)
3. Build JAR artifact (`_build-image.yaml`)
4. Build/push Docker image to ECR (`_push-image.yaml`)
5. Deploy new task definition to ECS and write deployed image tag to SSM (`_deploy-image.yaml`)

Key deployment contracts:

- AWS region: `eu-south-2`
- ECS cluster/service naming: `socially-<env>-cluster`, `socially-<env>-service`
- SSM parameter updated on deploy: `/config/socially/backend/container/image-version`
- **Cognito backend client:** the task sets `COGNITO_BACKEND_CLIENT_ID` from Terraform and injects **`COGNITO_BACKEND_CLIENT_SECRET_JSON`** (JSON credentials) from Secrets Manager.
- **OAuth redirect:** when CloudFront is enabled, Terraform passes **`AUTH_REDIRECT_URI`** (same URL as the Cognito SPA callback) so the `dev` profile matches Hosted UI.
- **Cognito Hosted UI:** **`COGNITO_HOSTED_DOMAIN`** is set to the Cognito auth domain base URL (`https://<prefix>.auth.<region>.amazoncognito.com`), bound as **`auth.oauth.hosted-domain`** in shared `application.yaml`.

## Repository Layout

```text
.
├── app/
├── commons/
├── donation/
│   ├── kernel/
│   ├── create/
│   ├── delete/
│   ├── get-by-id/
│   ├── update/
│   └── find/
├── .github/workflows/
├── Dockerfile
├── Dockerfile.dev
├── docker-compose.yml
├── build.gradle.kts
└── settings.gradle.kts
```
