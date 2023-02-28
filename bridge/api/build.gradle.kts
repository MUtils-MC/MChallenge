plugins {
    `kotlin-script`
    `core-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
}

group = "de.miraculixx.bridge.api"
setProperty("module_name", "bridge")