plugins {
    `kotlin-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla"))
    implementation(project(":challenges:api"))
}