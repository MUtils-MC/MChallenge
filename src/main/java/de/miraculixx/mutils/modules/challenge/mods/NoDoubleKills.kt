package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class NoDoubleKills : Challenge {
    override val challenge = Modules.NO_DOUBLE_KILL
    private var lastEntity = EntityType.PLAYER
    private val lastEntities = HashMap<UUID, EntityType>()
    private var global = true

    override fun start(): Boolean {
        val conf = ConfigManager.getConfig(Configs.MODULES)
        global = conf.getBoolean("NO_DOUBLE_KILL.Global")
        return true
    }

    override fun stop() {
        lastEntities.clear()
    }

    override fun register() {
        onKill.register()
    }

    override fun unregister() {
        onKill.unregister()
    }


    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        val entity = it.entity
        if (entity !is LivingEntity) return@listen
        if (it.damager !is Player) return@listen
        val player = it.damager as Player
        if ((entity.health - it.finalDamage) <= 0) {
            //KILL
            val type = entity.type
            if (global) {
                if (lastEntity == type) {
                    //DOUBLE KILL
                    doubleKill(entity, player)
                }
                lastEntity = type
            } else {
                val uuid = player.uniqueId
                if (lastEntities[uuid] == type) {
                    //DOUBLE KILL
                    doubleKill(entity, player)
                }
                lastEntities[uuid] = type
            }
        }
    }

    private fun doubleKill(entity: LivingEntity, player: Player) {
        player.damage(99.0)
        broadcast(msg("modules.ch.noDoubleKill.failed", player, entity.name))
        entity.isGlowing = true
        entity.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 200, false, false))
    }
}