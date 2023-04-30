package de.miraculixx.challenge.addon

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.kpaper.event.listen
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent

class DamageOnChest : Challenge {
    private val damage: Int

    init {

    }

    override fun register() {

    }

    override fun unregister() {

    }

    private val onChestClick = listen<PlayerInteractEvent> {
        val block = it.clickedBlock ?: return@listen
        if (block.type != Material.CHEST) return@listen
        it.player.damage()
    }
}