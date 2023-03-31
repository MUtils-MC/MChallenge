
plugins {
    id("fabric-loom")
//    id("io.github.juuxel.loom-quiltflower")
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings(loom.officialMojangMappings())

    modCompileOnly("net.fabricmc:fabric-loader:0.14.18")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.76.0+1.19.4")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.2+kotlin.1.8.10")
    modImplementation(include("net.kyori:adventure-platform-fabric:5.5.0")!!)
}