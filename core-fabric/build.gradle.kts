plugins {
    `kotlin-script`
    `adventure-script`
    `loom-script`
    `core-script`
    `shadow-script`
}

dependencies {
    modImplementation(include("net.kyori:adventure-platform-fabric:5.5.1")!!)
    implementation(project(":vanilla"))
}