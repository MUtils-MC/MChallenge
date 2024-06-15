package de.miraculixx.mchallenge.modules.mods.simple.stackLimit

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

class StackLimit : Challenge {
    private val stackLimit: Int

    init {
        val settings = challenges.getSetting(Challenges.STACK_LIMIT).settings
        stackLimit = settings["limit"]?.toInt()?.getValue() ?: 1
    }

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
            player.inventory.forEach { item ->
                item?.editMeta {
                    it.setMaxStackSize(stackLimit)
                }
            }
        }
        return true
    }

    override fun stop() {
        onlinePlayers.forEach { player ->
            player.inventory.forEach { item ->
                item?.editMeta {
                    it.setMaxStackSize(item.type.maxStackSize)
                }
            }
        }
    }

    override fun register() {
        onPickUp.register()
        onClick.register()
    }

    override fun unregister() {
        onPickUp.unregister()
        onClick.unregister()
    }

    private val onPickUp = listen<PlayerAttemptPickupItemEvent>(register = false) {
        it.item.itemStack.editMeta { meta -> meta.setMaxStackSize(stackLimit) }
    }

    private val onClick = listen<InventoryClickEvent>(register = false) {
        // Villager trading is bugged - Waiting for papers unset method?
        it.currentItem?.editMeta { meta -> meta.setMaxStackSize(stackLimit) }
    }
}