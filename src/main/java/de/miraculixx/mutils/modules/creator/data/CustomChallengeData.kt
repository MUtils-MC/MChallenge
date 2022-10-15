package de.miraculixx.mutils.modules.creator.data

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.creator.ActionProvider
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.modules.creator.jsonInstance
import de.miraculixx.mutils.utils.text.consoleMessage
import de.miraculixx.mutils.utils.text.consoleWarn
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.axay.kspigot.languageextensions.kotlinextensions.createIfNotExists
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readText

class CustomChallengeData(val uuid: UUID, pluginVersion: String, override val challenge: Modules = Modules.CUSTOM_CHALLENGE) : Challenge {
    private val path = Path("plugins/MUtils/Creator/$uuid.json").toAbsolutePath()
    val eventData = HashMap<CreatorEvent, EventData>()
    private val listener = HashMap<CreatorEvent, CustomChallengeListener<*>>()
    var item = ItemStack(Material.STRUCTURE_VOID)

    var data = ChallengeData(
        "Name Placeholder",
        "Challenge description placeholder",
        "Error",
        Author("MUtils", UUID.randomUUID()),
        Versions(Bukkit.getMinecraftVersion(), pluginVersion),
        emptyList()
    )

    override fun register() {
        listener.values.forEach {
            it.register()
        }
    }

    override fun unregister() {
        listener.values.forEach {
            it.unregister()
        }
    }

    fun loadConfig(): Boolean {
        val config = path.toFile()
        if (!config.exists() || !config.isFile || !config.canRead()) {
            consoleWarn("Failed to load Custom-Challenge $uuid")
            consoleWarn("Path: $path")
            return false
        }
        val jsonString = path.readText()
        return try {
            data = jsonInstance.decodeFromString(jsonString)
            data.events.forEach { event ->
                eventData[event.event] = event.data
                listener[event.event] = ActionProvider.buildListener(event)
            }
            data.events = emptyList()
            item = CreatorManager.getChallengeItem(this)
            true
        } catch (e: Exception) {
            item = CreatorManager.getChallengeItem(this)
            false
        }
    }

    fun saveConfig() {
        data.events = buildList {
            eventData.forEach { (event, data) ->
                add(Event(event, data))
            }
        }

        val jsonString = jsonInstance.encodeToString(data)
        val config = path.toFile()
        if (!config.isFile) config.delete()
        if (!config.exists()) config.createIfNotExists()
        config.writeText(jsonString)
    }

    fun update() {
        item = CreatorManager.getChallengeItem(this)
        eventData.forEach { (creatorEvent, eventData) ->
            listener[creatorEvent] = ActionProvider.buildListener(Event(creatorEvent, eventData))
        }
        saveConfig()
    }

    fun delete() {
        path.toFile().delete()
        consoleMessage("Deleted Custom Challenge ${data.name}")
        consoleMessage("Path: $path")
    }
}