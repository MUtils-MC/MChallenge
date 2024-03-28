package de.miraculixx.mchallenge.modules.mods.simple.disabled

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mvanilla.messages.namespace
import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

class Disabled: Challenge {
    private val damage: Double
    private val blockBreaking: Boolean
    private val blockPlace: Boolean
    private val crafting: Boolean
    private val pickupXP: Boolean
    private val pickupItem: Boolean
    private val trading: Boolean

    init {
        val settings = challenges.getSetting(Challenges.DISABLED).settings
        damage = settings["damage"]?.toInt()?.getValue()?.toDouble() ?: 0.0

        val blockSection = settings["block"]?.toSection()?.getValue()
        blockBreaking = blockSection?.get("break")?.toBool()?.getValue() ?: false
        blockPlace = blockSection?.get("place")?.toBool()?.getValue() ?: false

        val interactSection = settings["interact"]?.toSection()?.getValue()
        crafting = interactSection?.get("craft")?.toBool()?.getValue() ?: false
        trading = interactSection?.get("trade")?.toBool()?.getValue() ?: false

        val pickupSection = settings["misc"]?.toSection()?.getValue()
        pickupXP = pickupSection?.get("xp")?.toBool()?.getValue() ?: false
        pickupItem = pickupSection?.get("items")?.toBool()?.getValue() ?: false
    }

    override fun register() {
        onBlockBreak.register()
        onBlockPlace.register()
        onCrafting.register()
        onXP.register()
        onItemPickup.register()
        onTrading.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
        onBlockPlace.unregister()
        onCrafting.unregister()
        onXP.unregister()
        onItemPickup.unregister()
        onTrading.unregister()
    }

    private val onBlockBreak = listen<BlockBreakEvent> {
        it.player.handle(blockBreaking, it, "disable.break")
    }

    private val onBlockPlace = listen<BlockPlaceEvent> {
        it.player.handle(blockPlace, it, "disable.place")
    }

    private val onCrafting = listen<PlayerInteractEvent> {
        val block = it.clickedBlock ?: return@listen
        if (block.type == Material.CRAFTING_TABLE) it.player.handle(crafting, it, "disable.craft")
    }

    private val onXP = listen<PlayerPickupExperienceEvent> {
        it.player.handle(pickupXP, it, "disable.xp")
    }

    private val onItemPickup = listen<PlayerAttemptPickupItemEvent> {
        it.player.handle(pickupItem, it, "disable.item")
    }

    private val onTrading = listen<PlayerInteractAtEntityEvent> {
        val type = it.rightClicked.type
        if (type == EntityType.WANDERING_TRADER || type == EntityType.VILLAGER) {
            it.player.handle(trading, it, "disable.trade")
            taskRunLater(1) { it.player.closeInventory() }
        }
    }

    private fun Player.handle(block: Boolean, it: Cancellable, key: String) {
        if (block) {
            it.isCancelled = true
            if (damage > 0.0) {
                if (health - damage <= 0.0) persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, key)
                damage(damage)
            }
        }
    }
}