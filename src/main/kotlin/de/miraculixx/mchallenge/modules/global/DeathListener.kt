package de.miraculixx.mchallenge.modules.global

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.mcommons.namespace
import org.bukkit.NamespacedKey
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType

object DeathListener {
    val key = NamespacedKey(namespace, "death.custom")

    private val onDeath = listen<PlayerDeathEvent> {
        val player = it.player
        val deathKey = player.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return@listen
        it.deathMessage(player.msg("event.death.$deathKey", listOf(it.player.name)))
        player.persistentDataContainer.remove(key)
    }
}