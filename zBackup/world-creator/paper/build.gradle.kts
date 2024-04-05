plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
    implementation(project(":core-paper")) // Paper specified Utils
    implementation(project(":kpaper-light"))
    api(project(":world-creator:api"))
}

group = "de.miraculixx.mwc"
setProperty("module_name", "mwc")

tasks {
    jar {
        archiveBaseName.set("WorldCreator")
    }
}