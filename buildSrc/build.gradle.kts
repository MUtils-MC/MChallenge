plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://server.bbkr.space/artifactory/libs-release/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"
    val kotlinVersion = "1.9.0"

    compileOnly(kotlin("gradle-plugin", kotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Fabric implementation
    implementation("net.fabricmc:fabric-loom:1.3-SNAPSHOT")
    implementation(pluginDep("io.github.juuxel.loom-quiltflower", "1.9.0"))

    // Paper implementation
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.5.0")
    implementation(pluginDep("xyz.jpenilla.run-paper", "1.1.0"))

    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation(pluginDep("com.modrinth.minotaur", "2.+"))
}