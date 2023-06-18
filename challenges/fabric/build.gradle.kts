plugins {
    `kotlin-script`
    `core-script`
    `fabric-script`
    `adventure-script`
    `shadow-script`
}

dependencies {
    implementation(project(":core-fabric", configuration = "namedElements"))
}