
plugins {
    `kotlin-script`
    `core-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
}

group = "de.miraculixx.challenges.api"
setProperty("module_name", "challenges")