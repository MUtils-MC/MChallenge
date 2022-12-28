rootProject.name = "MUtils"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

// Cores
include("vanilla")
include("core-paper")
include("core-fabric")

include("custom-challenges:paper")
include("custom-challenges:fabric")

include("timer:paper")
include("timer:fabric")

include("challenges:paper")
include("challenges:fabric")

// 3rd party
include("kpaper-light")
