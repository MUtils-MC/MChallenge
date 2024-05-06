package de.miraculixx.mchallenge.modules.mods.misc.inTime

import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mcommons.text.cError
import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.prefix
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class InTimeEntity(
    startDuration: Duration,
    val entity: Entity,
    private val isPlayer: Boolean
) {
    var isRunning = true
    var duration = startDuration
    private val bossBar = if (isPlayer) BossBar.bossBar(getTime(), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS) else null

    init {
        bossBar?.addViewer(entity)
    }

    fun getTime(): Component {
        val format = duration.toComponents { minutes, seconds, _ ->
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
        return if (isRunning) cmp(format, getColor())
        else cmp("$format paused", cHighlight, italic = true)
    }

    fun remove() {
        bossBar?.removeViewer(entity) ?: entity.remove()
        task?.cancel()
    }

    private fun getColor(): TextColor {
        return if (duration.inWholeSeconds <= 30) cError
        else NamedTextColor.BLUE
    }

    private fun updateDisplay() {
        if (bossBar != null) bossBar.name(getTime())
        else entity.customName(getTime())
    }

    private val task = task(true, 20, 20) {
        if (!isRunning) {
            updateDisplay()
            return@task
        }

        if (entity.isDead) {
            if (!isPlayer) remove()
            it.cancel()
            return@task
        }

        // Time runs out
        if (duration <= Duration.ZERO) {
            if (isPlayer) {
                (entity as Player).damage(999.0)
                broadcast(prefix, "event.inTime.noTime", listOf(entity.name))
                entity.playSound(entity, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f)
            } else {
                val loc = entity.location
                loc.getNearbyPlayers(50.0).forEach { p ->
                    p.spawnParticle(Particle.WITCH, loc.add(0.0, 0.2, 0.0), 20, 0.1, 0.1, 0.1, 0.1)
                    p.playSound(loc, Sound.ENTITY_VEX_DEATH, 0.7f, 0.1f)
                }
                entity.remove()
            }
            it.cancel()
            return@task
        }

        duration -= 1.seconds
        updateDisplay()
    }
}