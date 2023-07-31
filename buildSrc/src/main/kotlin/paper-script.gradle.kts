import BuildConstants.minecraftVersion

plugins {
    kotlin("jvm")
//    id("io.papermc.paperweight.userdev")
//    id("xyz.jpenilla.run-paper")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
//    paperweight.paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-shade:9.0.3")
    compileOnly("dev.jorel:commandapi-bukkit-kotlin:9.0.3")
    compileOnly("org.spigotmc:spigot:1.20.1-R0.1-SNAPSHOT")
}

//tasks {
//    assemble {
//        dependsOn(reobfJar)
//    }
//}
