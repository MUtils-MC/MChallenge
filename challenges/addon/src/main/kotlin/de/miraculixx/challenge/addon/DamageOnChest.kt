package de.miraculixx.challenge.addon

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import java.util.UUID

class DamageOnChest(uuid: UUID) : Challenge {
    private val damage: Int

    init {
        val settings = AddonManager.getSettings(uuid).settings
        damage = settings["damage"]?.toInt()?.getValue() ?: 2
    }

    override fun register() {
        onChestClick.register()
    }

    override fun unregister() {
        onChestClick.unregister()
    }

    private val onChestClick = listen<PlayerInteractEvent>(register = false) {
        val block = it.clickedBlock ?: return@listen
        if (block.type != Material.CHEST) return@listen
        it.player.damage(damage.toDouble())
    }
}