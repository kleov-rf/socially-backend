import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.socially.auth.kernel.infrastructure.right"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":auth:kernel:domain"))
    implementation(project(":user:create:application"))
    implementation(project(":user:find-by-federated-identity:application"))
    implementation(project(":user:federated-identity:link:application"))
    implementation(project(":user:update-profile:application"))
    implementation(project(":user:kernel:domain"))

    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.oauth2.resource.server)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
