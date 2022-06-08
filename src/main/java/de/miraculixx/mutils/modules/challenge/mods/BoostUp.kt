package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerMoveEvent

class BoostUp : Challenge() {
    override val challenge = Modules.BOOST_UP
    private var radius: Double = 4.0
    private var boost: Double = 0.5
    private var mode: Boolean = true

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        radius = c.getDouble("BOOST_UP.Radius")
        boost = c.getInt("BOOST_UP.Boost") / 10.0
        mode = c.getBoolean("BOOST_UP.Mode")
        return true
    }

    override fun stop() {}

    override fun register() {
        onMove.register()
    }

    override fun unregister() {
        onMove.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val player = it.player
        player.getNearbyEntities(radius, radius, radius).forEach { entity ->
            if (entity is LivingEntity || mode)
                entity.velocity = entity.velocity.clone().setY(boost)
        }
    }
}