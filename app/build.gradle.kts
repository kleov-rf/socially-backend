plugins {
    java
    id("org.springframework.boot") version "4.0.0-RC2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.socially"
version = "0.0.1-SNAPSHOT"
description = "Backend for Socially platform"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":donation:create:infrastructure:left"))
    implementation(project(":donation:delete:infrastructure:left"))
    implementation(project(":donation:get-by-id:infrastructure:left"))
    implementation(project(":donation:update:infrastructure:left"))
    implementation(project(":donation:find:infrastructure:left"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("io.cucumber:cucumber-java:7.22.0")
    testImplementation("io.cucumber:cucumber-spring:7.22.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.22.0")
    testImplementation("org.junit.platform:junit-platform-suite:1.11.4")

    testImplementation("org.testcontainers:testcontainers:2.0.3")
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.3")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.3")
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    filter {
        excludeTestsMatching("*CucumberTestRunner*")
        isFailOnNoMatchingTests = false
    }

    systemProperty("spring.profiles.active", System.getProperty("spring.profiles.active", "test"))

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Task to run Cucumber integration tests with Testcontainers PostgreSQL
tasks.register<Test>("testIntegration") {
    group = "verification"
    description = "Run Cucumber integration tests against real PostgreSQL (Testcontainers)"

    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    useJUnitPlatform()

    filter {
        includeTestsMatching("*CucumberTestRunner*")
    }

    dependsOn("testClasses")
}
