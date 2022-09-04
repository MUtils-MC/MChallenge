package de.miraculixx.mutils.modules.creator

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.mm
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleMessage
import de.miraculixx.mutils.utils.text.consoleWarn
import de.miraculixx.mutils.utils.text.emptyComponent
import de.miraculixx.mutils.utils.text.lore
import kotlinx.serialization.json.Json
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.io.path.Path

val jsonInstance = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

object CreatorManager {
    var status = ChallengeStatus.STOPPED
    private val challenges = ArrayList<CustomChallengeData>()
    private val activeChallenges = ArrayList<UUID>()

    fun isActive(uuid: UUID): Boolean {
        return activeChallenges.contains(uuid)
    }

    fun getActive(): List<CustomChallengeData> {
        return challenges.filter { activeChallenges.contains(it.uuid) }
    }

    fun setActive(uuid: UUID, active: Boolean) {
        if (active) {
            if (!activeChallenges.contains(uuid))
                activeChallenges.add(uuid)
        } else {
            activeChallenges.remove(uuid)
        }
    }

    fun getChallenge(id: Int): CustomChallengeData? {
        return challenges.getOrNull(id)
    }

    fun getChallenge(uuid: UUID): CustomChallengeData? {
        return challenges.firstOrNull { it.uuid == uuid }
    }

    fun getAllChallenges(): List<CustomChallengeData> {
        return challenges.toList()
    }

    fun addChallenge(challenge: CustomChallengeData) {
        challenges.add(challenge)
    }

    fun removeChallenge(challenge: CustomChallengeData) {
        challenges.remove(challenge)
        activeChallenges.remove(challenge.uuid)
        challenge.delete()
        val config = ConfigManager.getConfig(Configs.CUSTOM_CHALLENGE)
        config.set(challenge.uuid.toString(), null)
    }

    fun getChallengeItem(challenge: CustomChallengeData): ItemStack {
        val uuid = challenge.uuid
        val data = challenge.data
        val material = Material.getMaterial(challenge.data.icon) ?: Material.STRUCTURE_VOID
        return itemStack(material) {
            meta {
                name = "§9§l${data.name}"
                persistentDataContainer.set(NamespacedKey(Manager, "gui.creator.challenge"), PersistentDataType.STRING, uuid.toString())
                lore(buildList {
                    add(mm.deserialize("<color:#3b3b3b>ID: $uuid").lore())
                    add(emptyComponent())
                    add(mm.deserialize("<grey>∙ <blue><u>Description").lore())
                    val words = data.description.split(' ')
                    repeat(words.size / 6 + 1) { row ->
                        val s = buildString {
                            repeat(6) { word ->
                                val w = words.getOrNull(word + (row * 6))
                                if (w != null) append("$w ")
                            }
                        }
                        add(Component.text("   $s").lore().color(NamedTextColor.GRAY))
                    }
                    add(emptyComponent())
                    add(mm.deserialize("<grey>∙ <blue><u>Info").lore())
                    add(mm.deserialize("   <grey>Native MC-Version <blue>${data.version.mc}").lore())
                    add(mm.deserialize("   <grey>Native MUtils-Version <blue>${data.version.mutils}").lore())
                    add(mm.deserialize("   <grey>Author - <blue>${data.author.name}").lore())
                })
            }
        }
    }

    fun modifyItem(challengeItem: ItemStack, count: Int, lore: List<Component>): ItemStack {
        val meta = challengeItem.itemMeta
        meta.customModel = count
        meta.lore(buildList {
            addAll(meta.lore() ?: emptyList())
            addAll(lore)
        })
        return itemStack(challengeItem.type) { itemMeta = meta }
    }

    fun saveAll() {
        challenges.forEach { ch ->
            ch.saveConfig()
        }
    }

    fun loadData() {
        //Wipe Data
        activeChallenges.clear()
        challenges.clear()

        //Reading Data
        val folder = Path("plugins/MUtils/Creator").toFile()
        if (!folder.isDirectory) folder.deleteOnExit()
        folder.mkdirs()
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) return@forEach
            if (!file.name.endsWith(".json")) return@forEach
            val uuid = try {
                UUID.fromString(file.nameWithoutExtension)
            } catch (_: IllegalArgumentException) {
                return@forEach
            }
            challenges.add(CustomChallengeData(uuid, Manager.description.version))
            consoleMessage("$prefix Custom Challenge $uuid loaded")
        }
        consoleMessage("$prefix §aSuccessfully loaded ${folder.listFiles()?.size} Custom Challenges!")

        //Loading Challenges
        challenges.forEach { customChallenge ->
            if (!customChallenge.loadConfig()) {
                consoleWarn("Failed to load Custom Challenge! (${customChallenge.uuid})")
                consoleWarn("Config file seems to be corrupted")
            }
        }
    }

    init {
        loadData()
        val config = ConfigManager.getConfig(Configs.CUSTOM_CHALLENGE)
        config.getKeys(false).forEach {
            val uuid = UUID.fromString(it)
            if (config.getBoolean(it))
                activeChallenges.add(uuid)
        }
    }
}
