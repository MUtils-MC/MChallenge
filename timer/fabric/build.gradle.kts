plugins {
    `kotlin-script`
    `core-script`
    `fabric-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla"))
    implementation(project(":core-fabric"))
}