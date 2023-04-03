package de.miraculixx.mchallenge.modules.mods.inTime

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.api.modules.challenges.ChallengeStatus
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mvanilla.messages.*
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

    fun getTime(): String {
        secString = if (sec <= 9) "0$sec"
        else sec.toString()

        minString = if (min <= 9) "0$min"
        else min.toString()

        return "$minString:$secString"
    }

    fun isRed(): Boolean {
        return min <= 0 && sec <= 30
    }

    fun setTime(min: Int, sec: Int) {
        this.sec = sec
        this.min = min
    }

    fun remove() {
        if (bossBar != null) {
            bossBar?.isVisible = false
            bossBar?.removeAll()
            key?.let { Bukkit.removeBossBar(it) }
        }
        if (!isPlayer) {
            entity?.remove()
        }
    }

    fun pauseTimer() {
        isRunning = false
    }

    fun resumeTimer() {
        isRunning = true
    }

    private fun inTime() {
        task(true, 20, 20) {
            // Timer Paused
            if (!isRunning || ChallengeManager.status == ChallengeStatus.RUNNING) {
                if (!isPlayer) entity?.customName(cmp("$time paused", cHighlight, italic = true))
                else bossBar?.setTitle("§9§o$time paused")
                return@task
            }

            // Remove Timer if entity is dead or despawned.
            // If Timer belongs to a player, it will be paused
            if (entity == null) {
                it.cancel()
                return@task
            }
            if (entity?.isDead == true) {
                if (isPlayer) {
                    isRunning = false
                    return@task
                }
                entity?.remove()
                remove()
                it.cancel()
                return@task
            }

            // Timer hits zero
            if (sec == 0 && min == 0) {
                if (!isPlayer) {
                    val loc = entity?.location ?: return@task
                    onlinePlayers.forEach { player ->
                        player.spawnParticle(Particle.SPELL_WITCH, loc.add(0.0, 0.2, 0.0), 20, 0.1, 0.1, 0.1, 0.1)
                        player.playSound(loc, Sound.ENTITY_VEX_DEATH, 0.7f, 0.1f)
                    }
                    entity?.remove()
                    isRunning = false
                    it.cancel()
                    return@task
                } else {
                    (entity as? LivingEntity)?.damage(999.0)
                    onlinePlayers.forEach { player ->
                        player.gameMode = GameMode.SPECTATOR
                        player.sendMessage(prefix + msg("event.inTime.noTime", listOf(player.name)))
                        player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f)
                    }
                    ChallengeManager.status = ChallengeStatus.PAUSED
                    isRunning = false
                    it.cancel()
                    return@task
                }
            }

            // Calculating time
            sec -= 1
            if (sec < 0) {
                min -= 1
                sec = 59
            }


            if (isPlayer) {
                //Spieler
                if (min == 0 && sec <= 30) {
                    // Manipulate the dragon to purge if timer is about to end (feels like insane clutch omg omg omg)
                    bossBar?.setTitle("§c$time")
                    if (entity is EnderDragon) (entity as EnderDragon).world.enderDragonBattle?.enderDragon?.phase = EnderDragon.Phase.LAND_ON_PORTAL
                } else bossBar?.setTitle("§6$time")
            }
        }
    }

    init {
        while (this.sec >= 60) {
            min++
            this.sec -= 60
        }
        isRunning = true
        if (isPlayer) {
            key = NamespacedKey(namespace, entity.name)
            bossBar = Bukkit.createBossBar(key!!, "§c...", BarColor.WHITE, BarStyle.SOLID)
            (bossBar as KeyedBossBar).isVisible = true
            (bossBar as KeyedBossBar).addPlayer(entity as Player)
        }
        inTime()
    }
}