package de.miraculixx.mchallenge.modules.mods.simple.noDoubleKill

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mchallenge.modules.global.DeathListener
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class NoDoubleKills : Challenge {
    private var lastEntity = EntityType.PLAYER
    private val lastEntities = HashMap<UUID, EntityType>()
    private var global: Boolean

    init {
        val settings = challenges.getSetting(Challenges.NO_DOUBLE_KILL).settings
        global = settings["global"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
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
        player.persistentDataContainer.set(DeathListener.key, PersistentDataType.STRING, "noDoubleKill")
        player.damage(999.0)
        entity.isGlowing = true
        entity.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 200, false, false))
    }
}