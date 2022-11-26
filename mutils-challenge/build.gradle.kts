plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
    implementation(project(":mutils-core", configuration = "namedElements"))
    implementation(project(":kpaper-light"))
}