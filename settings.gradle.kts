rootProject.name = "MUtils"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

// Cores
include("vanilla")
include("core-paper")
include("core-fabric")

// API Bridge
include("bridge")

// Custom Challenges
include("custom-challenges:paper")
include("custom-challenges:fabric")

// Timer
include("timer:api")
include("timer:global")
include("timer:paper")
include("timer:fabric")

// Challenges
include("challenges:paper")
include("challenges:fabric")
include("challenges:api")

// World Creator
include("world-creator:paper")
include("world-creator:fabric")
include("world-creator:api")

// 3rd party
include("kpaper-light")
