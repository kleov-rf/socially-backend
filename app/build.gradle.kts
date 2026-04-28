plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
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
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jackson.databind)
    testRuntimeOnly(libs.h2)

    testImplementation(libs.bundles.cucumber)
    testImplementation(libs.junit.platform.suite)

    testImplementation(libs.bundles.testcontainers)
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
