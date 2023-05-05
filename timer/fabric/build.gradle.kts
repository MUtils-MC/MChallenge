plugins {
    `kotlin-script`
    `core-script`
    `fabric-script`
    `adventure-script`
}

dependencies {
    implementation(include(project(":vanilla"))!!)
    implementation(include(project(":core-fabric"))!!)
}