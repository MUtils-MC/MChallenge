
plugins {
    kotlin("jvm")
}

val adventureVersion = "4.12.0"
dependencies {
//    modImplementation(include("net.kyori:adventure-platform-fabric:5.6.1")!!)
    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-gson:$adventureVersion")
}