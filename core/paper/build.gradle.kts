plugins {
    `kotlin-script`
    `paper-script`
    `core-script`
    `shadow-script`
}

dependencies {
    implementation(project(":vanilla"))
    implementation(project(":kpaper-light"))
}