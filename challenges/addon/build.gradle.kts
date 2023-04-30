
plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
    implementation(project(":kpaper-light"))

    implementation(project(":challenges:api")) // Internal API
}


group = "de.miraculixx.challenges.addon"
setProperty("module_name", "challenge-addon")