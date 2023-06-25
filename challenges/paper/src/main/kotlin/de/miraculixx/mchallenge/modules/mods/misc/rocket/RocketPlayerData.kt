package de.miraculixx.mchallenge.modules.mods.misc.rocket

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mvanilla.messages.cmp
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class RocketPlayerData(private val player: Player) {
    private val bar = BossBar.bossBar(cmp("Fuel"), 1f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS)
    private var breaker = false
    private var boosting = false

    fun startBoost() {
        boosting = true
    }

    fun stopBoost() {
        boosting = false
    }

    fun stop() {
        breaker = true
    }

    private fun scheduler() {
        var soundTicks = 0
        task(false, 0, 1) {
            if (breaker) {
                it.cancel()
                return@task
            }
            soundTicks++

            val current = bar.progress()
            if (boosting) {
                bar.progress((current - 0.005f).coerceAtLeast(0f))
                when (bar.progress()) {
                    0f -> TODO("BOOM")
                    in 0f..0.3f -> bar.color(BossBar.Color.RED)
                }
                player.velocity = player.velocity.add(Vector(.0, .5, .0))
                val location = player.location.clone().add(.0,.4,.0)
                if (soundTicks == 4) {
                    player.playSound(player, Sound.ENTITY_WOLF_SHAKE, 1f, 0.8f)
                    onlinePlayers.forEach { p ->
                        p.spawnParticle(Particle.FIREWORKS_SPARK, location, 3, 0.2,0.2,0.2,0.01)
                    }
                    soundTicks = 0
                }
            } else {
                if (current == 1f) return@task
                bar.progress((current + 0.016f).coerceAtMost(1f))
                if (bar.progress() > 0.3f) bar.color(BossBar.Color.YELLOW)
            }
        }
    }

    init {
        scheduler()
        player.showBossBar(bar)
    }
}