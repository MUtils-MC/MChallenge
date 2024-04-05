plugins {
    `kotlin-script`
    `core-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
}

group = "de.miraculixx.mwc.api"
setProperty("module_name", "mwc")