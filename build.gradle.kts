plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.sonarqube)
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
    apply(plugin = "jacoco")

    sonar {
        properties {
            property("sonar.sources", "src/main")
            property("sonar.exclusions", "**/*.sql")
            // Only set test and coverage report paths when tests exist in this module.
            if (project.file("src/test").exists()) {
                property("sonar.tests", "src/test")
                property(
                    "sonar.coverage.jacoco.xmlReportPaths",
                    layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.path,
                )
            }
        }
    }

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
        // Always generate the XML report after unit tests so Sonar can import coverage.
        finalizedBy("jacocoTestReport")
    }

    tasks.named("jacocoTestReport") {
        dependsOn("test")
        (this as org.gradle.testing.jacoco.tasks.JacocoReport).reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
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
