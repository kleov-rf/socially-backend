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
    implementation(project(":donation:infrastructure:left"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("io.cucumber:cucumber-java:7.22.0")
    testImplementation("io.cucumber:cucumber-spring:7.22.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.22.0")
    testImplementation("org.junit.platform:junit-platform-suite:1.11.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
