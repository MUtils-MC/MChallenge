import BuildConstants.minecraftVersion

plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
}

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
