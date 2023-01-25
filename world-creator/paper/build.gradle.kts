plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
    implementation(project(":core-paper")) // Paper specified Utils
    implementation(project(":kpaper-light"))
    implementation(project(":world-creator:api"))
}