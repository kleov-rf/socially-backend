# Socially Backend

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-See%20LICENSE-lightgrey.svg)](LICENSE)

Backend service for the Socially platform — a charitable donation management system.

## Table of Contents

- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [API Reference](#api-reference)
- [Docker](#docker)
- [Contributing](#contributing)

## Architecture

This project follows **Hexagonal Architecture** (also known as Ports and Adapters), combined with **Domain-Driven Design (DDD)** principles and a **CQRS-lite** pattern for separating read and write operations.

### Architectural Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                              APP MODULE                               │
│                      (Spring Boot Application)                        │
│                                                                       │
│  ┌───────────────────── DONATION MODULE ─────────────────────────┐   │
│  │                                                                │   │
│  │  ┌──────────────────────────────────────────────────────────┐ │   │
│  │  │                INFRASTRUCTURE:LEFT                        │ │   │
│  │  │         (Primary/Driving Adapters - HTTP API)             │ │   │
│  │  │                                                           │ │   │
│  │  │   • CreateDonationController    POST /api/donations       │ │   │
│  │  │   • GetDonationController       GET  /api/donations/{id}  │ │   │
│  │  └─────────────────────────┬────────────────────────────────┘ │   │
│  │                            │                                   │   │
│  │                            ▼ depends on                        │   │
│  │  ┌──────────────────────────────────────────────────────────┐ │   │
│  │  │                    APPLICATION                            │ │   │
│  │  │               (Use Cases / Business Logic)                │ │   │
│  │  │                                                           │ │   │
│  │  │   Inbound Ports:                                          │ │   │
│  │  │   • CreateDonationUseCase                                 │ │   │
│  │  │   • FindDonationByIdUseCase                               │ │   │
│  │  │                                                           │ │   │
│  │  │   Handlers (Implementations):                             │ │   │
│  │  │   • CreateDonationCommandHandler                          │ │   │
│  │  │   • FindDonationByIdQueryHandler                          │ │   │
│  │  └─────────────────────────┬────────────────────────────────┘ │   │
│  │                            │                                   │   │
│  │                            ▼ depends on                        │   │
│  │  ┌──────────────────────────────────────────────────────────┐ │   │
│  │  │                      DOMAIN                               │ │   │
│  │  │          (Core Business Rules - Framework Free)           │ │   │
│  │  │                                                           │ │   │
│  │  │   Entities:      Donation                                 │ │   │
│  │  │   Value Objects: Id, Title, Description                   │ │   │
│  │  │   Outbound Ports: DonationRepository                      │ │   │
│  │  └─────────────────────────▲────────────────────────────────┘ │   │
│  │                            │                                   │   │
│  │                            │ implements                        │   │
│  │  ┌─────────────────────────┴────────────────────────────────┐ │   │
│  │  │                INFRASTRUCTURE:RIGHT                       │ │   │
│  │  │        (Secondary/Driven Adapters - Persistence)          │ │   │
│  │  │                                                           │ │   │
│  │  │   • InMemoryDonationRepository                            │ │   │
│  │  └──────────────────────────────────────────────────────────┘ │   │
│  │                                                                │   │
│  └────────────────────────────────────────────────────────────────┘   │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

### Key Architectural Principles

| Principle | Description |
|-----------|-------------|
| **Dependency Rule** | Dependencies always point inward. Domain has no external dependencies. |
| **Port/Adapter Pattern** | Ports define contracts; adapters implement them for specific technologies. |
| **Domain Isolation** | Business logic is isolated from frameworks, making it testable and portable. |
| **CQRS-Lite** | Separate command (write) and query (read) handlers for clarity. |

## Project Structure

```
socially-backend/
├── app/                                    # Spring Boot application module
│   └── src/
│       ├── main/java/.../app/
│       │   └── SociallyBackendApplication.java
│       └── test/
│           ├── java/.../cucumber/          # Cucumber BDD tests
│           └── resources/features/         # Gherkin feature files
│
├── donation/                               # Donation bounded context
│   ├── domain/                             # Core domain layer
│   │   └── src/main/java/.../domain/
│   │       ├── entity/                     # Domain entities
│   │       │   └── Donation.java
│   │       ├── valueobject/                # Value objects
│   │       │   ├── Id.java
│   │       │   ├── Title.java
│   │       │   └── Description.java
│   │       └── port/right/                 # Outbound ports (driven)
│   │           └── DonationRepository.java
│   │
│   ├── application/                        # Application layer (use cases)
│   │   └── src/main/java/.../application/
│   │       ├── port/left/                  # Inbound ports (driving)
│   │       │   ├── CreateDonationUseCase.java
│   │       │   └── FindDonationByIdUseCase.java
│   │       ├── create/                     # Create donation use case
│   │       │   ├── CreateDonationCommandHandler.java
│   │       │   ├── input/
│   │       │   └── mapper/
│   │       └── get/                        # Get donation use case
│   │           ├── FindDonationByIdQueryHandler.java
│   │           ├── input/
│   │           ├── output/
│   │           └── mapper/
│   │
│   └── infrastructure/
│       ├── left/                           # Primary adapters (driving)
│       │   └── src/main/java/.../left/adapter/http/
│       │       ├── create/
│       │       │   └── CreateDonationController.java
│       │       └── get/
│       │           └── GetDonationController.java
│       │
│       └── right/                          # Secondary adapters (driven)
│           └── src/main/java/.../right/adapter/persistence/
│               └── InMemoryDonationRepository.java
│
├── gradle/                                 # Gradle wrapper
├── build.gradle.kts                        # Root build configuration
├── settings.gradle.kts                     # Module definitions
├── Dockerfile                              # Production container
└── Dockerfile.dev                          # Development container
```

### Module Dependencies

```
app
 └── donation:infrastructure:left
      ├── donation:application
      │    └── donation:domain
      └── donation:infrastructure:right
           └── donation:domain
```

## Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 25 |
| **Framework** | Spring Boot | 4.0.0-RC2 |
| **Build Tool** | Gradle (Kotlin DSL) | 8.x |
| **Code Quality** | Spotless (Google Java Format) | 8.0.0 |
| **Testing** | JUnit 5, Mockito, Cucumber | Latest |
| **Utilities** | Lombok | Latest |
| **Container** | Docker (Eclipse Temurin) | JRE 25 Alpine |

## Getting Started

### Prerequisites

- **Java 25** or higher
- **Gradle 8.x** (or use the included Gradle wrapper)
- **Docker** (optional, for containerized deployment)

### Clone the Repository

```bash
git clone https://github.com/your-org/socially-backend.git
cd socially-backend
```

### Build the Project

```bash
# Using Gradle wrapper (recommended)
./gradlew build

# On Windows
gradlew.bat build
```

### Code Formatting

The project uses [Spotless](https://github.com/diffplug/spotless) with Google Java Format:

```bash
# Check formatting
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply
```

## Running the Application

### Local Development

```bash
./gradlew :app:bootRun
```

The application will start on `http://localhost:8080`.

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Module Tests

```bash
# Domain tests only
./gradlew :donation:domain:test

# Application layer tests
./gradlew :donation:application:test

# Integration/Cucumber tests
./gradlew :app:test
```

### Test Reports

After running tests, reports are available at:
- **Unit Tests:** `app/build/reports/tests/test/index.html`
- **Cucumber Reports:** `app/build/reports/cucumber/cucumber.html`

### Testing Strategy

| Layer | Test Type | Tools |
|-------|-----------|-------|
| Domain | Unit Tests | JUnit 5 |
| Application | Unit Tests | JUnit 5, Mockito |
| Infrastructure | Integration Tests | Spring Boot Test |
| End-to-End | BDD/Acceptance | Cucumber |

## API Reference

### Base URL

```
http://localhost:8080/api
```

### Endpoints

#### Create Donation

```http
POST /api/donations
Content-Type: application/json

{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Winter Clothes Drive",
  "description": "Collecting warm clothes for homeless shelters"
}
```

**Response:** `201 Created`

#### Get Donation by ID

```http
GET /api/donations/{id}
```

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Winter Clothes Drive",
  "description": "Collecting warm clothes for homeless shelters"
}
```

**Response:** `404 Not Found` (if donation doesn't exist)

## Docker

### Build the Image

```bash
# First, build the JAR
./gradlew :app:bootJar

# Build Docker image
docker build -t socially-backend:latest .
```

### Run the Container

```bash
docker run -p 8080:8080 socially-backend:latest
```

### Development Container

```bash
docker build -f Dockerfile.dev -t socially-backend:dev .
docker run -p 8080:8080 socially-backend:dev
```

### Container Features

- **Non-root user:** Runs as `spring` user for security
- **Health checks:** Built-in container health monitoring
- **JVM optimization:** Container-aware memory settings (`-XX:+UseContainerSupport`)

## Contributing

### Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Run `./gradlew spotlessApply` before committing
- Write meaningful commit messages

### Pull Request Guidelines

1. Create a feature branch from `main`
2. Ensure all tests pass (`./gradlew test`)
3. Ensure code is formatted (`./gradlew spotlessCheck`)
4. Update documentation if needed
5. Submit PR with clear description

### Architecture Guidelines

When adding new features:

1. **Start with the Domain** — Define entities, value objects, and ports
2. **Implement Use Cases** — Create command/query handlers in the application layer
3. **Add Adapters** — Implement HTTP controllers (left) and persistence (right)
4. **Write Tests** — Unit tests for domain/application, integration tests for infrastructure

## License

See the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <sub>Built with ❤️ using Hexagonal Architecture</sub>
</p>
