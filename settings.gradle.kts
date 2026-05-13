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
include("commons:observability")
include("auth:kernel:domain")
include("auth:kernel:infrastructure:left")
include("auth:login:application")
include("auth:login:infrastructure:left")
include("auth:callback:domain")
include("auth:callback:application")
include("auth:callback:infrastructure:left")
include("auth:callback:infrastructure:right")
include("auth:me:application")
include("auth:me:infrastructure:left")
include("auth:logout:domain")
include("auth:logout:application")
include("auth:logout:infrastructure:left")
include("auth:logout:infrastructure:right")
include("auth:refresh:domain")
include("auth:refresh:application")
include("auth:refresh:infrastructure:left")
include("auth:refresh:infrastructure:right")
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
include("user:kernel:domain")
