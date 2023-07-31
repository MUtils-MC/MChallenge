plugins {
    `kotlin-script`
    `paper-script`
    `core-script`
    `shadow-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla"))
    implementation(project(":kpaper-light"))
}