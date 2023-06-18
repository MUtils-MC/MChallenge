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
    minecraft("com.mojang:minecraft:1.19.4")
    mappings(loom.officialMojangMappings())

    val silkVersion = "1.9.8"
    modImplementation("net.silkmc:silk-commands:$silkVersion")
    modImplementation("net.silkmc:silk-core:$silkVersion")
    modImplementation("net.silkmc:silk-nbt:$silkVersion")
    modImplementation("net.silkmc:silk-persistence:$silkVersion")
    modImplementation("net.kyori:adventure-platform-fabric:5.8.0")
    modImplementation("net.fabricmc:fabric-loader:0.14.19")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.80.0+1.19.4")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.4+kotlin.1.8.21")
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