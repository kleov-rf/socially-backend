plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

group = "com.socially.auth.refresh.application"
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
    implementation(project(":auth:kernel:domain"))
    implementation(project(":auth:kernel:infrastructure:right"))
    implementation(project(":auth:refresh:domain"))
    implementation(project(":user:create:application"))
    implementation(project(":user:find-by-email:application"))
    implementation(project(":user:kernel:domain"))

    implementation(libs.spring.context)

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
