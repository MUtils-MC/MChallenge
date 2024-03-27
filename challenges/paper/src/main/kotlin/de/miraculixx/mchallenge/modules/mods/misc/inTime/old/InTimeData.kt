package de.miraculixx.mchallenge.modules.mods.misc.inTime.old

import de.miraculixx.challenge.api.modules.challenges.ChallengeStatus
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class InTimeData(var sec: Int, entity: Entity, private var isPlayer: Boolean) {
    private var isRunning: Boolean
    private var min = 0
    private var secString: String? = null
    private var minString: String? = null
    private var entity: Entity? = entity
    private var bossBar: BossBar? = null

    fun getTime(): Component {
        secString = if (sec <= 9) "0$sec"
        else sec.toString()

        minString = if (min <= 9) "0$min"
        else min.toString()

        val time = "$minString:$secString"
        return if (!isRunning) cmp("$time paused", cHighlight, italic = true)
        else cmp(time, if (isRed()) cError else NamedTextColor.BLUE)
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
            bossBar?.removeViewer(entity as Player)
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
            if (!isRunning || ChallengeManager.status != ChallengeStatus.RUNNING) {
                if (!isPlayer) entity?.customName(getTime())
                else bossBar?.name(getTime())
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
                if (min == 0 && sec <= 30) {
                    // Manipulate the dragon to purge if timer is about to end (feels like insane clutch omg omg omg)
                    bossBar?.name(getTime())
                    if (entity is EnderDragon) (entity as EnderDragon).world.enderDragonBattle?.enderDragon?.phase = EnderDragon.Phase.LAND_ON_PORTAL
                } else bossBar?.name(getTime())
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
            bossBar = BossBar.bossBar(getTime(), 1f, net.kyori.adventure.bossbar.BossBar.Color.RED, BossBar.Overlay.PROGRESS)
            (entity as Player).showBossBar(bossBar!!)
        }
        inTime()
    }
}