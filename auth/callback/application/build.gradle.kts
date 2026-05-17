plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

group = "com.socially.auth.callback.application"
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
    implementation(project(":auth:callback:domain"))
    implementation(project(":user:kernel:domain"))

    implementation(libs.spring.context)
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
