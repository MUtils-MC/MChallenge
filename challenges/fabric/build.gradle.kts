plugins {
    `kotlin-script`
    `loom-script`
    `adventure-script`
    `shadow-script`
}

dependencies {
    implementation(project(":core:fabric", configuration = "namedElements"))
}