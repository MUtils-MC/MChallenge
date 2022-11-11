package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent


class SplitHP : Challenge {
    override val challenge = Modules.SPLIT_HP

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue!!
        }
        return true
    }

    override fun stop() {}
    override fun register() {
        onDamage.register()
        onReg.register()
    }
    override fun unregister() {
        onDamage.unregister()
        onReg.unregister()
    }

    private val onDamage = listen<EntityDamageEvent>(register = false, priority = EventPriority.LOWEST) {
        if (it.isCancelled) return@listen
        if (it.cause == EntityDamageEvent.DamageCause.CUSTOM) return@listen
        if (it.entity !is Player) return@listen
        val player = it.entity as Player
        val herzen = if ((player.health - it.finalDamage) <= 0.0) 0.0
        else player.health - it.finalDamage
        onlinePlayers.forEach { player1 ->
            if (!Spectator.isSpectator(player1.uniqueId)) {
                if (player1 != player) {
                    player1.damage(0.01)
                    player1.health = herzen
                }
            }
        }
    }

    private val onReg = listen<EntityRegainHealthEvent>(register = false, priority = EventPriority.LOWEST) {
        if (it.isCancelled) return@listen
        if (it.entity !is Player) return@listen
        if (Spectator.isSpectator(it.entity.uniqueId)) return@listen
        it.isCancelled = true
        val player = it.entity as Player
        val maxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue ?: 20.0
        val herzen = if ((player.health + it.amount) >= maxHP) maxHP
        else player.health + it.amount
        onlinePlayers.forEach { player1 ->
            if (!Spectator.isSpectator(player1.uniqueId)) {
                if (player1 != player) player1.health = herzen
            }
        }
    }
}