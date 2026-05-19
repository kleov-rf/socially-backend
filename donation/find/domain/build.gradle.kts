plugins {
    `java-library`
    alias(libs.plugins.spring.dependency.management)
}

group = "com.socially.donation.find.domain"
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
    api(project(":donation:kernel:domain"))

    compileOnly(libs.spring.web)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
