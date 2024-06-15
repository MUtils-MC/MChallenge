import dex.plugins.outlet.v2.util.ReleaseType
import groovy.json.JsonSlurper
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.modrinth.minotaur") version "2.+"
    id("io.github.dexman545.outlet") version "1.6.1"

    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://dl.cloudsmith.io/public/matyrobbrt/javanbt/maven/")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")

    // Kotlin libraries
    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.+")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.+")

    // MC Libraries
    implementation("de.miraculixx:mc-commons:1.0.1")
    implementation("de.miraculixx:kpaper-light:1.2.1")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.4.2")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.4.2")
    implementation("io.github.matyrobbrt:javanbt:0.0.3")

    // Internal APIs
    implementation("de.miraculixx:mbridge:1.0.0")
    implementation("de.miraculixx:challenge-api:1.5.0")

    // External APIs
    compileOnly("de.miraculixx:mweb:1.1.0")
    compileOnly("de.miraculixx:timer-api:1.1.3")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    shadowJar {
        dependencies {
            include {
                it.moduleGroup == "de.miraculixx" || it.moduleGroup == "dev.jorel"
            }
        }
        relocate("dev.jorel.commandapi", "de.miraculixx.mchallenge.commandapi")
    }
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/data/")
    }
}

bukkit {
    main = "$group.${projectName.lowercase()}.${projectName}"
    apiVersion = "1.16"
    foliaSupported = foliaSupport
    website = "https://mutils.net/ch/info"
    authors = listOf("Miraculixx", "NoRisk")

    // Optionals
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    depend = listOf()
    softDepend = listOf("MTimer", "MWeb")
    libraries = listOf(
        "io.ktor:ktor-client-core-jvm:2.3.7",
        "io.ktor:ktor-client-cio-jvm:2.3.7"
    )
}

modrinth {
    token.set(properties["modrinthToken"] as String)
    projectId.set(properties["modrinthProjectId"] as? String ?: properties["name"] as String)
    versionNumber.set(version as String)
    versionType.set(properties["publishState"] as String)

    uploadFile.set(tasks.jar)
    versionName = "MChallenge - ${properties["version"]}"
    outlet.mcVersionRange = properties["supportedVersions"] as String
    outlet.allowedReleaseTypes = setOf(ReleaseType.RELEASE)
    gameVersions.addAll(outlet.mcVersions())
    loaders.addAll(buildList {
        add("paper")
        add("purpur")
    })
    dependencies {
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("mweb")
    }

    changelog = "- Fix error caused by latest Paper API changes regarding brigadier commands"

    syncBodyFrom = rootProject.file(".github/assets/README-Modrinth.md").readText()
}


//
// Custom Tasks
//

fun get(key: String, map: Map<String, Any>): String? {
    var route: Any? = map
    key.split('.').forEach {
        if (route is Map<*, *>) {
            route = (route as Map<*, *>)[it]
        } else {
            return null
        }
    }
    return route?.toString()
}

data class Challenge(
    val key: String,
    val version: String,
    val item: String?,
    val block: String?,
    val head: String?,
    val tags: List<String>,
    val settings: List<String> = emptyList(),
    val new: Boolean = false,
    val preview: String? = null
)

@Suppress("UNCHECKED_CAST")
val craftReadme = task("craftReadme") {
    group = "publishing"
    doLast {
        val challengeFile = File("data/challenges.json")
        val challengeSlurper = JsonSlurper().parse(challengeFile) as List<Map<String, Any>>
        val challengeList = challengeSlurper.map {
            Challenge(
                it["key"].toString(),
                it["version"].toString(),
                it["item"] as? String,
                it["block"] as? String,
                it["head"] as? String,
                it["tags"] as List<String>,
                (it["settings"] as? List<String>) ?: emptyList(),
                it["new"] == true,
                it["preview"] as? String
            )
        }.sortedByDescending { it.tags.contains("FREE") }

        val languageFile = File("data/language/mchallenge/en.yml")
        val yaml = Yaml(DumperOptions().apply { defaultFlowStyle = DumperOptions.FlowStyle.BLOCK })
        val languageMap = yaml.load<Map<String, Any>>(languageFile.inputStream())

        val challengeString = buildString {
            challengeList.forEach { challenge ->
                val name = get("items.ch.${challenge.key}.n", languageMap)
                val iconUrl = when {
                    challenge.item != null -> "https://mutils.net/images/mc/grab/items/${challenge.item}.png"
                    challenge.block != null -> "https://mutils.net/images/mc/grab/rendered/${challenge.block}.png"
                    challenge.head != null -> "https://mc-heads.net/head/${challenge.head}"
                    else -> "https://mutils.net/images/mc/grab/items/barrier.png"
                }
                append("<details><summary><b>$name</b> ▪ <img src='$iconUrl' width='18'></summary>\n")
                append(get("items.ch.${challenge.key}.l", languageMap)?.replace("<br>", " "))
                append("\n\n---\n\n⚙\uFE0F **Settings**")
                if (challenge.settings.isEmpty()) append("\n- `No settings`")
                else {
                    challenge.settings.forEach { setting ->
                        val settingName = get("items.chS.${challenge.key}.$setting.n", languageMap)
                        val description = get("items.chS.${challenge.key}.$setting.l", languageMap)?.replace("<br>", " ")
                        append("\n- `$settingName`")
                        if (description != null) append(" - $description")
                    }
                }
                append("\n\n\uD83C\uDFF7\uFE0F **Tags**")
                challenge.tags.forEach tags@{ tag ->
                    if (tag == "FREE") return@tags
                    val tagName = tag[0] + tag.substring(1).lowercase()
                    val description = get("tags.$tag.l", languageMap)?.replace("<br>", " ")
                    append("\n- `$tagName` - $description")
                }
                challenge.preview?.let { append("\n\n![Challenge Preview]($it)") }
                if (!challenge.tags.contains("FREE")) append("<br>*Currently requires full access*")
                append("\n</details>")
            }
        }

        val githubRemover = Regex("<!-- github_exclude\\.start -->(.*?)<!-- github_exclude\\.end -->", RegexOption.DOT_MATCHES_ALL)
        val sourceFile = File(".github/assets/README.md")
        val readmeModrinth = File(".github/assets/README-Modrinth.md")
        val readmeFile = File("README.md")
        var readmeContent = sourceFile.readText()
        readmeContent = readmeContent.replace("<!-- challenges -->", challengeString)
        readmeContent = readmeContent.replace("<!-- challenge-count -->", (challengeList.size + 10).toString()) // Multi Challenges: Disabler 6, Mirror 4, Force Collect 3
        readmeModrinth.writeText(readmeContent)
        readmeFile.writeText(githubRemover.replace(readmeContent, ""))
    }
}

