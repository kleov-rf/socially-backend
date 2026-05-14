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
    implementation(project(":commons:observability"))
    implementation(project(":auth:login:infrastructure:left"))
    implementation(project(":auth:callback:infrastructure:left"))
    implementation(project(":auth:me:infrastructure:left"))
    implementation(project(":auth:logout:infrastructure:left"))
    implementation(project(":auth:refresh:infrastructure:left"))
    implementation(project(":auth:kernel:infrastructure:left"))
    implementation(project(":donation:create:infrastructure:left"))
    implementation(project(":donor:create:infrastructure:right"))
    implementation(project(":donor:find-by-user-id:infrastructure:right"))
    implementation(project(":donor:find-by-id:infrastructure:right"))
    implementation(project(":donation:delete:infrastructure:left"))
    implementation(project(":donation:get-by-id:infrastructure:left"))
    implementation(project(":donation:update:infrastructure:left"))
    implementation(project(":donation:find:infrastructure:left"))
    implementation(project(":user:create:infrastructure:right"))
    implementation(project(":user:find-by-email:infrastructure:right"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.logstash.logback.encoder)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.boot.starter.oauth2.resource.server)
    testImplementation(libs.jackson.databind)
    testRuntimeOnly(libs.h2)

    testImplementation(libs.bundles.cucumber)
    testImplementation(libs.junit.platform.suite)

    testImplementation(libs.bundles.testcontainers)
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    val activeProfile =
        System.getenv("SPRING_PROFILES_ACTIVE")
            ?: System.getProperty("spring.profiles.active")
            ?: "local"
    systemProperty("spring.profiles.active", activeProfile)

    if (System.getenv("DB_HOST") == null) {
        environment("DB_HOST", "localhost")
    }
    if (System.getenv("DB_PORT") == null) {
        environment("DB_PORT", "5432")
    }
    if (System.getenv("DB_NAME") == null) {
        environment("DB_NAME", "socially")
    }
    if (System.getenv("DB_USERNAME") == null) {
        environment("DB_USERNAME", "postgres")
    }
    if (System.getenv("DB_PASSWORD") == null) {
        environment("DB_PASSWORD", "postgres")
    }
    if (System.getenv("LOCAL_DB_HOST") == null) {
        environment("LOCAL_DB_HOST", "localhost")
    }
    if (System.getenv("LOCAL_DB_PORT") == null) {
        environment("LOCAL_DB_PORT", "5432")
    }
    if (System.getenv("LOCAL_DB_NAME") == null) {
        environment("LOCAL_DB_NAME", "socially")
    }
    if (System.getenv("LOCAL_DB_USERNAME") == null) {
        environment("LOCAL_DB_USERNAME", "postgres")
    }
    if (System.getenv("LOCAL_DB_PASSWORD") == null) {
        environment("LOCAL_DB_PASSWORD", "postgres")
    }
    if (System.getenv("LOCAL_COGNITO_USE_MINISTACK") == null &&
        "true".equals(System.getenv("COGNITO_USE_MINISTACK"), ignoreCase = true)
    ) {
        environment("LOCAL_COGNITO_USE_MINISTACK", "true")
    }
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

    systemProperty("spring.profiles.active", System.getProperty("spring.profiles.active", "test"))

    filter {
        includeTestsMatching("com.socially.app.cucumber.CucumberTestRunner")
    }

    dependsOn("testClasses")
}

tasks.named<Jar>("jar") {
    enabled = false
}
