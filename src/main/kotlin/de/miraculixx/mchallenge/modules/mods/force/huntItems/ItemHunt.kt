package de.miraculixx.mchallenge.modules.mods.force.huntItems

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.items.getMaterials
import de.miraculixx.mchallenge.modules.challenges.interfaces.HuntChallenge
import de.miraculixx.mchallenge.utils.serializer.Serializer
import de.miraculixx.mcommons.extensions.enumOf
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class ItemHunt : Challenge, HuntChallenge<Material>("itemhunt", "item_hunt") {
    override val typeName = "Item"
    override val allEntries = getMaterials(true)
    override val maxEntries = allEntries.size
    override val remainingEntries = mutableListOf<Material>()
    override var currentTarget: Material? = null
    override val serializer = object : Serializer<Material> {
        override fun toString(data: Material) = data.name
        override fun toObject(data: String) = enumOf<Material>(data) ?: Material.STONE
    }

    override fun register() {
        onCollect.register()
        onInvClose.register()
    }

    override fun unregister() {
        onCollect.unregister()
        onInvClose.unregister()
    }

    override fun start(): Boolean {
        startHunt()
        return true
    }

    override fun stop() {
        stopHunt()
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        val entity = it.entity
        if (entity !is Player) return@listen
        if (currentTarget == it.item.itemStack.type) collectItem(entity)
    }

    private val onInvClose = listen<InventoryCloseEvent>(register = false) {
        val player = it.player
        if (player !is Player) return@listen
        it.inventory.forEach { item -> if (item?.type == currentTarget) collectItem(player) }
    }

    private fun collectItem(player: Player) {
        nextEntry(player.name, player)
    }

    override fun getTranslationKey() = currentTarget?.translationKey()?.let { "<lang:$it>" }
}