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

// API Bridge
include("bridge")

// Custom Challenges
include("custom-challenges:paper")
include("custom-challenges:fabric")

// Timer
include("timer:paper")
include("timer:fabric")
include("timer:api")

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
