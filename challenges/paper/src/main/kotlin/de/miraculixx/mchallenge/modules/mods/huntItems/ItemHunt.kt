package de.miraculixx.mchallenge.modules.mods.huntItems

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mchallenge.commands.ModuleCommand
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.mods.huntMob.HuntObject
import de.miraculixx.mcore.utils.getMaterials
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import java.io.File

class ItemHunt : Challenge, HuntObject<Material> {
    private val dataFile = File("${MChallenge.configFolder.path}/data/item_hunt.json")
    private var currentItem: Material? = null
    override val maxEntries = getMaterials(true).size
    override val remainingEntries = mutableListOf<Material>()
    override val blacklist = mutableListOf<Material>()
    override val bar = BossBar.bossBar(cmp("Waiting for server..."), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    override fun register() {
        onCollect.register()
        onInvClose.register()
    }

    override fun unregister() {
        onCollect.unregister()
        onInvClose.unregister()
    }

    override fun start(): Boolean {
        val content = json.decodeFromString<ItemHuntData>(dataFile.readJsonString(true))
        blacklist.addAll(content.blacklist)
        if (content.remaining.isEmpty()) remainingEntries.addAll(getMaterials(true))
        else remainingEntries.addAll(content.remaining)
        remainingEntries.removeAll(blacklist)
        if (content.target != null) currentItem = content.target
        else {
            currentItem = remainingEntries.random()
            remainingEntries.remove(currentItem)
        }
        onlinePlayers.forEach { it.showBossBar(bar) }
        calcBar(getCurrentEntryName())
        val cmdClass = ItemHuntCommand(this)
        val cmdInstance = PluginManager.getCommand("itemhunt") ?: return false
        cmdInstance.setExecutor(cmdClass)
        cmdInstance.tabCompleter = cmdClass
        return true
    }

    override fun stop() {
        if (!dataFile.exists()) dataFile.parentFile.mkdirs()
        dataFile.writeText(json.encodeToString(ItemHuntData(currentItem, remainingEntries)))
        onlinePlayers.forEach { it.hideBossBar(bar) }
        ModuleCommand("itemhunt")
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        val entity = it.entity
        if (entity !is Player) return@listen
        if (currentItem == it.item.itemStack.type) collectItem(entity)
    }

    private val onInvClose = listen<InventoryCloseEvent>(register = false) {
        val player = it.player
        if (player !is Player) return@listen
        it.inventory.forEach { item -> if (item?.type == currentItem) collectItem(player) }
    }

    private fun collectItem(player: Player) {
        nextEntry(player.name, player)
    }

    override fun nextEntry(playerName: String, audience: Audience) {
        broadcast(prefix + msg("event.itemHunt.collect", listOf(playerName, currentItem?.name?.fancy() ?: "")))
        audience.playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.MASTER, 1f, 1.2f))
        val size = remainingEntries.size
        currentItem = if (size == 0) {
            broadcast(prefix + msg("event.itemHunt.success"))
            ChallengeManager.stopChallenges()
            null
        } else remainingEntries.random()
        remainingEntries.remove(currentItem)
        calcBar(getCurrentEntryName())
    }

    override fun getCurrentEntryName() = currentItem?.name

    @Serializable
    private data class ItemHuntData(
        val target: Material? = null,
        val remaining: List<Material> = emptyList(),
        val blacklist: List<Material> = emptyList()
    )
}