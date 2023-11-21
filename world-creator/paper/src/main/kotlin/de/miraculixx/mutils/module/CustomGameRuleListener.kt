package de.miraculixx.mutils.module

import de.miraculixx.challenge.api.data.CustomGameRule
import de.miraculixx.kpaper.event.listen
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.UUID

object CustomGameRuleListener {
    // Use a separate access list for performance reasons
    val blockedPhysics: MutableSet<UUID> = mutableSetOf()

    private val onDamage = listen<EntityDamageByEntityEvent> {
        val damager = it.damager
        val target = it.entity
        if (damager is Player && target is Player) {
            val worldData = WorldManager.getWorldData(damager.world.uid) ?: return@listen
            if (worldData.customGameRules[CustomGameRule.PVP] == false) it.isCancelled = true
        }
    }

    private val onPhysics = listen<BlockPhysicsEvent> {
//        if (blockedPhysics.contains(it.block.world.uid)) it.isCancelled = true
    }
}