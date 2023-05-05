import BuildConstants.minecraftVersion

plugins {
    id("fabric-loom")
    `kotlin-script`
    `core-script`
    `adventure-script`
}

repositories {
    mavenCentral()
    maven {
        name = "JitPack"
        setUrl("https://jitpack.io")
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.silkmc:silk-commands:1.9.8")
    modImplementation("net.fabricmc:fabric-loader:0.14.19")
    modImplementation("net.kyori:adventure-platform-fabric:5.8.0")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.77.0+1.19.4")
    modImplementation("me.lucko", "fabric-permissions-api", "0.2-SNAPSHOT")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += "-Xskip-prerelease-check"
        }
    }
}

dependencies {
    implementation(project(":vanilla"))
}