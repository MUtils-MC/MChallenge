package de.miraculixx.mchallenge.modules.global

import de.miraculixx.kpaper.event.listen
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.namespace
import org.bukkit.NamespacedKey
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType

object DeathListener {
    val key = NamespacedKey(namespace, "death.custom")

    @Suppress("unused")
    private val onDeath = listen<PlayerDeathEvent> {
        val player = it.player
        val deathKey = player.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return@listen
        it.deathMessage(msg("event.death.$deathKey", listOf(it.player.name)))
        player.persistentDataContainer.remove(key)
    }
}