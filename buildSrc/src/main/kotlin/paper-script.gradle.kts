import BuildConstants.minecraftVersion

plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-bukkit-shade:9.3.+")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.3.+")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
