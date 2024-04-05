import dex.plugins.outlet.v2.util.ReleaseType
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("io.papermc.paperweight.userdev") version "1.5.+"
    id("xyz.jpenilla.run-paper") version "2.2.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.modrinth.minotaur") version "2.+"
    id("io.github.dexman545.outlet") version "1.6.1"
}

group = properties["group"] as String
version = properties["version"] as String
description = properties["description"] as String

val gameVersion by properties
val foliaSupport = properties["foliaSupport"] as String == "true"
val projectName = properties["name"] as String

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    // Kotlin libraries
    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.+")

    // MC Libraries
    library("de.miraculixx:mc-commons:1.0.1")
    library("de.miraculixx:kpaper-light:1.2.1")
    library("dev.jorel:commandapi-bukkit-shade:9.3.+")
    library("dev.jorel:commandapi-bukkit-kotlin:9.3.+")

    // Internal APIs
    library("de.miraculixx:mbridge:1.0.0")
    library("de.miraculixx:challenge-api:1.5.0")

    // External APIs
    compileOnly("de.miraculixx:mweb:1.1.0")
    compileOnly("de.miraculixx:timer-api:1.1.3")
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

sourceSets {
    main {
        resources.srcDirs("$rootDir/challenges/data/")
    }
}

bukkit {
    main = "$group.${projectName.lowercase()}.${projectName}"
    apiVersion = "1.16"
    foliaSupported = foliaSupport
    website = "https://mutils.net/ch"
    authors = listOf("Miraculixx", "NoRisk")

    // Optionals
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    depend = listOf()
    softDepend = listOf("MTimer", "MWeb")
    commands.create("mobhunt")
    commands.create("itemhunt")
}

modrinth {
    token.set(properties["modrinthToken"] as String)
    projectId.set(properties["modrinthProjectId"] as? String ?: projectName)
    versionNumber.set(version as String)
    versionType.set("release") // Can also be `beta` or `alpha`
    uploadFile.set(tasks.jar)
    outlet.mcVersionRange = properties["supportedVersions"] as String
    outlet.allowedReleaseTypes = setOf(ReleaseType.RELEASE)
    gameVersions.addAll(outlet.mcVersions())
    loaders.addAll(buildList {
        add("paper")
        add("purpur")
        if (foliaSupport) add("folia")
    })
    dependencies {
        optional.project("timer")
        optional.project("mweb")
    }

    // Project sync
    syncBodyFrom = rootProject.file("README.md").readText()
}
