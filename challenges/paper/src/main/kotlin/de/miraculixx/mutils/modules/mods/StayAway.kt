package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent

class StayAway : Challenge {
    override val challenge = Challenge.STAY_AWAY
    private var distance: Double? = null

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        distance = c.getDouble("STAY_AWAY.Distance")
        return true
    }

    override fun stop() {
        distance = null
    }

    override fun register() {
        onMove.register()
    }

    override fun unregister() {
        onMove.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        if (distance == null) return@listen
        val player = it.player
        val gm = player.gameMode
        if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR) return@listen
        if (Spectator.isSpectator(player.uniqueId)) return@listen
        player.getNearbyEntities(distance!! + 2, distance!! + 2, distance!! + 2).forEach { e ->
            if (e is LivingEntity) {
                if (e is Player && Spectator.isSpectator(e.uniqueId)) return@forEach
                if (e.location.distance(player.location) <= distance!!) {
                    player.damage(999.0)
                    broadcast(msg("modules.ch.stayAway.tooClose", player, e.type.name))
                    e.isGlowing = true
                } else player.damage(0.01)
                return@listen
            }
        }
    }
}