plugins {
    java
    id("org.springframework.boot") version "4.0.0-RC2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "8.0.0"
}

group = "com.socially"
version = "0.0.1-SNAPSHOT"
description = "Backend for Socially platform"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

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
