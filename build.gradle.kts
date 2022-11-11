@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm") version "1.7.10"
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.20"
}

group = "de.miraculixx"
version = ""

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core-jvm:2.1.3")
    implementation("io.ktor:ktor-client-cio-jvm:2.1.3")
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    implementation("net.axay:kspigot:1.19.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}
tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}