package de.miraculixx.mutils.modules.challenge.mods.inTime

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.runnables.task
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.boss.KeyedBossBar
import org.bukkit.entity.*

class InTimeData(var sec: Int, entity: Entity, private var isPlayer: Boolean) {
    private var isRunning: Boolean
    private var min = 0
    private var secString: String? = null
    private var minString: String? = null
    private var entity: Entity? = entity
    private var bossBar: BossBar? = null
    private var key: NamespacedKey? = null

    private var time: String? = null
        get() {
            secString = if (sec <= 9) {
                "0$sec"
            } else {
                sec.toString()
            }
            minString = if (min <= 9) {
                "0$min"
            } else {
                min.toString()
            }
            field = "$minString:$secString"
            return field
        }

    init {
        while (this.sec >= 60) {
            min++
            this.sec -= 60
        }
        isRunning = true
        if (isPlayer) {
            key = NamespacedKey(Main.INSTANCE, entity.name)
            bossBar = Bukkit.createBossBar(key!!, "§c...", BarColor.WHITE, BarStyle.SOLID)
            (bossBar as KeyedBossBar).isVisible = true
            (bossBar as KeyedBossBar).addPlayer(entity as Player)
        }
        inTime()
    }

    fun setTime(min: Int, sec: Int) {
        this.sec = sec
        this.min = min
    }

    fun remove() {
        if (bossBar != null) {
            bossBar!!.isVisible = false
            bossBar!!.removeAll()
            key?.let { Bukkit.removeBossBar(it) }
        }
        if (!isPlayer) {
            entity!!.remove()
        }
    }

    private fun inTime() {
        task(true, 20, 20) {
            if (!isRunning || challenges != ChallengeStatus.RUNNING) {
                if (!isPlayer) {
                    entity!!.customName = "§9§o$time paused"
                } else {
                    bossBar?.setTitle("§9§o$time paused")
                }
                return@task
            }
            if (entity == null) {
                it.cancel()
                return@task
            }
            if (entity!!.isDead) {
                if (isPlayer) {
                    isRunning = false
                    return@task
                }
                entity!!.remove()
                remove()
                it.cancel()
                return@task
            }
            if (sec == 0 && min == 0) {
                if (!isPlayer) {
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.spawnParticle(Particle.SPELL_WITCH, entity!!.location.add(0.0, 0.2, 0.0), 20, 0.1, 0.1, 0.1, 0.1)
                        player.playSound(entity!!.location, Sound.ENTITY_VEX_DEATH, 0.7f, 0.1f)
                    }
                    entity!!.remove()
                    isRunning = false
                    it.cancel()
                    return@task
                } else {
                    (entity as LivingEntity).damage(999.0)
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.gameMode = GameMode.SPECTATOR
                        player.sendMessage(msg("module.challenges.inTime.noTime", player))
                        player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f)
                    }
                    challenges = ChallengeStatus.PAUSED
                    isRunning = false
                    it.cancel()
                    return@task
                }
            }
            sec -= 1
            if (sec < 0) {
                //Down
                min -= 1
                sec = 59
            }
            if (!isPlayer) {
                var playerNearby = false
                for (nearbyEntity in entity!!.getNearbyEntities(6.0, 6.0, 6.0)) {
                    if (nearbyEntity is Player) {
                        entity!!.isCustomNameVisible = true
                        playerNearby = true
                    }
                }
                if (!playerNearby) entity!!.isCustomNameVisible = false
                if (min == 0 && sec <= 30) {
                    entity!!.customName = "§c$time"
                } else {
                    entity!!.customName = "§6$time"
                }
            } else {
                //Spieler
                if (min == 0 && sec <= 30) {
                    bossBar?.setTitle("§c$time")
                    if (entity!!.type == EntityType.ENDER_DRAGON) {
                        entity!!.world.enderDragonBattle!!.enderDragon!!.phase = EnderDragon.Phase.LAND_ON_PORTAL
                    }
                } else {
                    bossBar?.setTitle("§6$time")
                }
            }
        }
    }
}