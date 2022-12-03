
plugins {
    id("fabric-loom")
    id("io.github.juuxel.loom-quiltflower")
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings(loom.layered {
        officialMojangMappings()
    })

    modCompileOnly("net.fabricmc:fabric-loader:0.12.11")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.68.0+1.19.2")
    modImplementation(include("net.kyori:adventure-platform-fabric:5.5.0")!!)
}