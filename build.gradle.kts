plugins {
    java
    id("org.springframework.boot") version "4.0.0-RC2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "8.0.0"
    id("org.sonarqube") version "7.2.3.7755"
}

group = "com.socially"
version = "0.0.1-SNAPSHOT"
description = "Backend for Socially platform"

allprojects {
    repositories {
        mavenCentral()
    }
}

sonar {
    properties {
        property("sonar.projectKey", "kleov-rf_socially-backend")
        property("sonar.organization", "kleov-rf")
    }
}

subprojects {
    apply(plugin = "java")

    // Use project path as artifact name to avoid filename collisions (e.g., multiple left/right/domain modules).
    base {
        archivesName = path.removePrefix(":").replace(':', '-')
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

spotless {
    java {
        target("**/*.java")
        importOrder()
        removeUnusedImports()
        googleJavaFormat("1.28.0")
        lineEndings = com.diffplug.spotless.LineEnding.UNIX
    }
}
