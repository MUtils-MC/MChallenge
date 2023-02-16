package de.miraculixx.mutils.modules.mods.huntItems

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.mutils.MChallenge
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.modules.Challenge
import de.miraculixx.mutils.modules.ChallengeManager
import de.miraculixx.mutils.utils.getMaterials
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import java.io.File

class ItemHunt: Challenge {
    override val challenge: Challenges = Challenges.MOB_HUNT
    private val dataFile = File("${MChallenge.configFolder.path}/data/item_hunt.json")
    private val bar = BossBar.bossBar(cmp("Waiting for server..."), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)
    private val maxItems = getMaterials(true)
    private val remainingItems: MutableList<Material> = mutableListOf()
    private var currentItem: Material? = null
    private val blacklist: MutableList<Material> = mutableListOf()

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
        if (content.remaining.isEmpty()) remainingItems.addAll(getMaterials(true))
        else remainingItems.addAll(content.remaining)
        if (content.target != null) currentItem = content.target
        else {
            currentItem = remainingItems.random()
            remainingItems.remove(currentItem)
        }
        return true
    }

    override fun stop() {
        val data = ItemHuntData(currentItem, remainingItems, blacklist)
        dataFile.writeText(json.encodeToString(data))
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {

    }

    private val onInvClose = listen<InventoryCloseEvent>(register = false) {

    }

    private fun collectItem() {

    }

    fun addBlacklist(material: Material) {
        blacklist.add(material)
        remainingItems.remove(material)
        calcBar()
    }

    fun removeBlacklist(material: Material) {
        if (blacklist.remove(material))
            remainingItems.add(material)
    }

    fun getBlacklist(): List<Material> {
        return blacklist
    }

    fun nextMob(playerName: String, audience: Audience) {
        broadcast(prefix + msg("event.itemHunt.collect", listOf(playerName, currentItem?.name?.fancy() ?: "")))
        audience.playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.MASTER, 1f, 1.2f))
        val size = remainingItems.size
        currentItem = if (size == 0) {
            broadcast(prefix + msg("event.itemHunt.success"))
            ChallengeManager.stopChallenges()
            null
        } else remainingItems.random()
        remainingItems.remove(currentItem)
        calcBar()
    }

    private fun calcBar() {
        val collectedAmount = ""
        //bar.name(miniMessages.deserialize("<gray>Target:</gray> <blue><b>$target</b></blue>  <dark_gray>(<gray><green>$collectedAmount</green>/<red>$maxEntities</red></gray>)</dark_gray>"))
    }

    @Serializable
    private data class ItemHuntData(
        val target: Material? = null,
        val remaining: List<Material> = emptyList(),
        val blacklist: List<Material> = emptyList()
    )
}