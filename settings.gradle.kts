rootProject.name = "MUtils"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

include("mutils-core")
include("mutils-challenge")
include("mutils-custom-challenge")
include("mutils-timer")

include("kpaper-light")