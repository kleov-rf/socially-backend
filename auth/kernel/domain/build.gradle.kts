plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

group = "com.socially.auth.kernel.domain"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("${libs.spring.boot.dependencies.bom.get().module}:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    api(libs.jackson.databind)
    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.oauth2.resource.server)

    compileOnly(libs.spring.web)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
