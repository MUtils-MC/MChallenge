package de.miraculixx.mchallenge.modules.mods.mirror

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mvanilla.messages.*
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.persistence.PersistentDataType

class Mirror : Challenge {
    private val mirrorHearts: Boolean
    private val mirrorHunger: Boolean
    private val mirrorPotion: Boolean
    private val mirrorHotbar: Boolean

    init {
        val settings = challenges.getSetting(Challenges.MIRROR).settings
        mirrorHearts = settings["hearts"]?.toBool()?.getValue() ?: false
        mirrorHunger = settings["food"]?.toBool()?.getValue() ?: false
        mirrorPotion = settings["potions"]?.toBool()?.getValue() ?: false
        mirrorHotbar = settings["hotbar"]?.toBool()?.getValue() ?: false
    }

    override fun register() {
        onHeartDown.register()
        onHeartUp.register()
        onChangeHunger.register()
        onHotbarChange.register()
        onPotion.register()
    }

    override fun unregister() {
        onHeartDown.unregister()
        onHeartUp.unregister()
        onChangeHunger.unregister()
        onHotbarChange.unregister()
        onPotion.unregister()
    }

    private val onHeartDown = listen<EntityDamageEvent>(register = false) {
        if (!mirrorHearts) return@listen
        val entity = it.entity
        if (entity !is Player) return@listen
        if (it.cause == EntityDamageEvent.DamageCause.CUSTOM) return@listen
        broadcast(prefix + cmp("${entity.name} got ") + cmp("${it.finalDamage}hp ", cHighlight) + cmp("damage from ") + cmp(it.cause.name, cHighlight))
        onlinePlayers.forEach { p ->
            if (p == entity) return@forEach
            if (Spectator.isSpectator(p.uniqueId)) return@forEach
            p.damage(0.001)
            p.health = (entity.health - it.finalDamage).coerceAtLeast(0.0)
        }
    }

    private val onHeartUp = listen<EntityRegainHealthEvent>(register = false) {
        if (!mirrorHearts) return@listen
        val entity = it.entity
        if (entity !is Player) return@listen
        if (it.regainReason == EntityRegainHealthEvent.RegainReason.CUSTOM) return@listen
        onlinePlayers.forEach { p ->
            if (p == entity) return@forEach
            if (Spectator.isSpectator(p.uniqueId)) return@forEach
            p.health = entity.health
        }
    }

    private val onChangeHunger = listen<FoodLevelChangeEvent>(register = false) {
        if (!mirrorHunger) return@listen
        val player = it.entity
        if (player !is Player) return@listen
        val currentValue = player.persistentDataContainer.get(NamespacedKey(namespace, "ch.mirror.food"), PersistentDataType.SHORT)
        if (currentValue == 1.toShort()) {
            player.persistentDataContainer.remove(NamespacedKey(namespace, "ch.mirror.food"))
            return@listen
        }
        onlinePlayers.forEach { p ->
            if (player == p) return@forEach
            if (Spectator.isSpectator(p.uniqueId)) return@forEach
            p.persistentDataContainer.set(NamespacedKey(namespace, "ch.mirror.food"), PersistentDataType.SHORT, 1.toShort())
            p.foodLevel = it.foodLevel
        }
    }

    private val onHotbarChange = listen<PlayerItemHeldEvent>(register = false) {
        if (!mirrorHotbar) return@listen
        val player = it.player
        val currentValue = player.persistentDataContainer.get(NamespacedKey(namespace, "ch.mirror.hotbar"), PersistentDataType.SHORT)
        if (currentValue == 1.toShort()) {
            player.persistentDataContainer.remove(NamespacedKey(namespace, "ch.mirror.hotbar"))
            return@listen
        }
        onlinePlayers.forEach { p ->
            if (player == p) return@forEach
            if (Spectator.isSpectator(p.uniqueId)) return@forEach
            p.persistentDataContainer.set(NamespacedKey(namespace, "ch.mirror.hotbar"), PersistentDataType.SHORT, 1.toShort())
            p.inventory.heldItemSlot = it.newSlot
        }
    }

    private val onPotion = listen<EntityPotionEffectEvent>(register = false) {
        if (!mirrorPotion) return@listen
        val entity = it.entity
        if (entity !is Player) return@listen
        if (it.cause == EntityPotionEffectEvent.Cause.PLUGIN) return@listen
        when (it.action) {
            EntityPotionEffectEvent.Action.ADDED -> {
                val effect = it.newEffect ?: return@listen
                onlinePlayers.forEach { p ->
                    if (p.shouldSkipped(entity)) return@forEach
                    p.addPotionEffect(effect)
                }
            }

            EntityPotionEffectEvent.Action.CHANGED -> {
                val newEffect = it.newEffect ?: return@listen
                val type = it.modifiedType
                onlinePlayers.forEach { p ->
                    if (p.shouldSkipped(entity)) return@forEach
                    p.removePotionEffect(type)
                    p.addPotionEffect(newEffect)
                }
            }

            EntityPotionEffectEvent.Action.CLEARED, EntityPotionEffectEvent.Action.REMOVED -> {
                val type = it.modifiedType
                onlinePlayers.forEach { p ->
                    if (p.shouldSkipped(entity)) return@forEach
                    p.removePotionEffect(type)
                }
            }
        }
    }

    private fun Player.shouldSkipped(source: Player): Boolean {
        return source == this || Spectator.isSpectator(source.uniqueId)
    }
}