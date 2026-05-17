plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

group = "com.socially.donation.delete.application"
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
    implementation(project(":donation:kernel:application"))
    implementation(project(":donation:delete:domain"))
    implementation(project(":donation:get-by-id:domain"))
    implementation(project(":donation:kernel:domain"))
    implementation(project(":user:kernel:domain"))

    implementation(libs.spring.context)
    compileOnly(libs.jakarta.validation.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(project(":auth:kernel:domain"))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
