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
- Postgres: `localhost:5432` (`postgres` / `postgres`, db `socially`)

### Option 2: Run from Gradle/IDE

Start Postgres separately (for example from compose), then:

```bash
./gradlew :app:bootRun
```

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
