package de.miraculixx.mchallenge.modules.mods.misc.rhythm.entity

import org.bukkit.entity.Player

class RPlayer(val player: Player): RLivingEntity(player) {
    override fun tick() {} // Do not tick player

    override fun onSpawn() {
        player.setGravity(false)
        normalize()
    }

    override fun stop() {
        player.setGravity(true)
    }
}