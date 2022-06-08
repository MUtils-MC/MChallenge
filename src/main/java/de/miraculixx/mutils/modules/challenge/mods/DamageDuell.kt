package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageDuell : Challenge() {
    override val challenge = Modules.DAMAGE_DUELL

    override fun start(): Boolean {
        return true
    }
    override fun stop() {}
    override fun register() {
        onMelee.register()
    }
    override fun unregister() {
        onMelee.unregister()
    }

    private val onMelee = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player && it.damager !is Projectile) return@listen
        if (it.damager is Projectile) {
            val shooter = (it.damager as Projectile).shooter ?: return@listen
            if (shooter !is Player) return@listen
        }
        val config = ConfigManager.getConfig(Configs.MODULES)
        val damage = it.finalDamage * (config.getDouble("Settings.DamageDuell.Percent") / 100.0)
        onlinePlayers.forEach { player ->
            if (!Spectator.isSpectator(player.uniqueId)) {
                if (player.health < 2) player.health += 0.01
                player.damage(0.01)
                if (player.health - damage >= 1) player.health -= damage
                else player.health = 1.0
            }
        }
    }
}