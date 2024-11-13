package de.miraculixx.mchallenge.utils

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mcommons.namespace
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class MobBossBar(entity: LivingEntity, color: BarColor, name: String) {
    private var entity: LivingEntity
    private var key: NamespacedKey
    private var bossBar: BossBar

    init {
        this.entity = entity
        key = NamespacedKey(namespace, entity.uniqueId.toString() + "-health")
        bossBar = Bukkit.createBossBar(key, name, color, BarStyle.SOLID)
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer)
        }
    }

    private fun removeMobBar() {
        bossBar.isVisible = false
        bossBar.removeAll()
        Bukkit.removeBossBar(key)
        onDamage.unregister()
        onRegen.unregister()
    }

    private fun update(change: Double) {
        if (entity.isDead) {
            removeMobBar()
            return
        }
        val max = entity.getAttribute(Attribute.MAX_HEALTH)?.baseValue
        val hp = entity.health.plus(change)
        bossBar.progress = hp / max!!
        bossBar.isVisible = true
    }

    // Update Events
    private val onDamage = listen<EntityDamageEvent> {
        if (it.entity == entity) update(it.finalDamage)
    }
    private val onRegen = listen<EntityRegainHealthEvent> {
        if (it.entity == entity) update(it.amount)
    }
}