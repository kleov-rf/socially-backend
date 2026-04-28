pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "socially-backend"

include("app")
include("donation:kernel:domain")
include("donation:kernel:infrastructure:right")
include("donation:create:domain")
include("donation:create:application")
include("donation:create:infrastructure:left")
include("donation:create:infrastructure:right")
include("donation:delete:domain")
include("donation:delete:application")
include("donation:delete:infrastructure:left")
include("donation:delete:infrastructure:right")
include("donation:get-by-id:domain")
include("donation:get-by-id:application")
include("donation:get-by-id:infrastructure:left")
include("donation:get-by-id:infrastructure:right")
include("donation:update:domain")
include("donation:update:application")
include("donation:update:infrastructure:left")
include("donation:update:infrastructure:right")
include("donation:find:domain")
include("donation:find:application")
include("donation:find:infrastructure:left")
include("donation:find:infrastructure:right")
