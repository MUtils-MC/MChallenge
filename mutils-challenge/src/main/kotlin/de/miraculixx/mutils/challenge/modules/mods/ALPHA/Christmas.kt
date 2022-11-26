@file:Suppress("unused")

package de.miraculixx.mutils.modules.challenge.mods.ALPHA

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.utils.activated
import de.miraculixx.mutils.utils.active
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.entity.*
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.random.Random

object Christmas {
    private var karma = 100
    private val obj = ChristmasObj()

    fun start() {
        karma = 100
        karmaChange(true, 100)
        onlinePlayers.forEach {
            obj.addPlayer(it)
        }
    }

    val onSpawn = listen<CreatureSpawnEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        if (it.entity is ArmorStand) return@listen
        if (it.entity.type == EntityType.PIG) {
            it.entity.getNearbyEntities(20.0, 20.0, 20.0).forEach { e -> if (e.type == EntityType.PIG) e.remove() }
            it.entity.remove()
            it.isCancelled = true
        }
        if (karma >= Random.nextInt(0, 100)) {
            it.entity.addScoreboardTag("HAPPY")
            it.entity.isGlowing = true
        }
    }

    val onJoin = listen<PlayerQuitEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        obj.addPlayer(it.player)
    }

    val onFire = listen<EntityCombustEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        it.entity.isVisualFire = false
        it.isCancelled = true
    }

    val onBreed = listen<EntityBreedEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        karmaChange(true, 1)
        obj.newStatus(1, it.entity.location)
    }

    val onTame = listen<EntityTameEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        karmaChange(true, 1)
        obj.newStatus(2, it.entity.location)
    }

    val onAttack = listen<EntityDamageByEntityEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        if (it.damager is Player && it.entity is LivingEntity) {
            val entity = it.entity as LivingEntity
            val player = it.damager as Player
            if (entity.scoreboardTags.contains("HAPPY")) {
                karmaChange(false, 1)
                obj.newStatus(-1, it.entity.location)
            }
            entity.removeScoreboardTag("HAPPY")
            when (karma) {
                in 90..100 -> {
                    player.sendMessage("§7[§6§l${it.entity.type.name.lowercase()}§7] §e${getMessage(1)}")
                }
                in 60..89 -> {
                    player.sendMessage("§7[§6§l${it.entity.type.name.lowercase()}§7] §e${getMessage(2)}")
                    if (entity is Monster) {
                        entity.target = it.damager as Player
                        entity.isAware = true
                    } else {
                        player.damage(Random.nextDouble(1.0, 4.0))
                    }
                }
                in 21..59 -> {
                    player.sendMessage("§7[§6§l${it.entity.type.name.lowercase()}§7] §e${getMessage(3)}")
                    if (entity is Monster) {
                        entity.target = it.damager as Player
                        entity.isAware = true
                        entity.attack(player)
                        entity.getNearbyEntities(30.0, 30.0, 30.0).forEach { homie ->
                            if (homie.type == entity.type && homie is Monster) {
                                homie.target = player
                            }
                        }
                    } else {
                        player.damage(Random.nextDouble(2.0, 5.0))
                    }
                }
                in 1..20 -> {
                    player.sendMessage("§7[§6§l${it.entity.type.name.lowercase()}§7] §e${getMessage(4)}")
                    if (entity is Monster) {
                        entity.target = it.damager as Player
                        entity.isAware = true
                        entity.attack(player)
                        entity.getNearbyEntities(50.0, 50.0, 50.0).forEach { homie ->
                            if (homie.type == entity.type && homie is Monster) {
                                homie.target = player
                            }
                        }
                    } else {
                        player.damage(Random.nextDouble(5.0, 8.0))
                    }
                }
                0 -> {
                    player.sendMessage("§7[§6§l${it.entity.type.name.lowercase()}§7] §e${getMessage(5)}")
                    if (entity is Monster) {
                        entity.target = it.damager as Player
                        entity.isAware = true
                        entity.attack(player)
                        entity.getNearbyEntities(50.0, 50.0, 50.0).forEach { homie ->
                            if (homie is Monster) {
                                homie.target = player
                            }
                        }
                    } else {
                        player.damage(Random.nextDouble(9.0, 11.0))
                    }
                }
            }
        }
    }

    private val onTarget = listen<EntityTargetLivingEntityEvent> {
        if (!active) return@listen
        if (!activated[Challenge.CHRISTMAS]!!) return@listen
        if (it.entity !is Player && it.target !is Player) it.target = null
        if (it.entity.scoreboardTags.contains("HAPPY")) it.target = null
    }

    private fun karmaChange(plus: Boolean, change: Int) {
        if (plus) {
            if (karma + change <= 100) karma += change
            else karma = 100
        } else {
            if (karma - change >= 0) karma -= change
            else karma = 0
        }
        obj.changeBar(karma)
    }

    private fun getMessage(level: Int): String {

        return ""
    }
}