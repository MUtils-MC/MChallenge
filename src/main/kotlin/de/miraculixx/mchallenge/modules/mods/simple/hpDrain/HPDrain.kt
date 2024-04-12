package de.miraculixx.mchallenge.modules.mods.simple.hpDrain

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mcommons.text.msg
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import org.bukkit.attribute.Attribute
import kotlin.time.Duration.Companion.seconds

class HPDrain: Challenge {
    private val percentage: Int
    private val delay: Int
    private var paused = true

    init {
        val settings = challenges.getSetting(Challenges.HP_DRAIN).settings
        percentage = settings["percentage"]?.toInt()?.getValue() ?: 50
        delay = settings["interval"]?.toInt()?.getValue() ?: (60 * 10)
    }

    override fun register() {
        paused = false
    }

    override fun unregister() {
        paused = true
    }

    override fun start(): Boolean {
        notifyStatus()
        return true
    }

    override fun stop() {
        task?.cancel()
        onlinePlayers.forEach { p ->
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
        }
    }

    private fun notifyStatus() {
        onlinePlayers.forEach { p ->
            val locale = p.language()
            p.sendMessage(prefix + locale.msg("event.hpDrain.current", listOf(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue?.toString() ?: "<i>Unknown<!i>")))
            p.sendMessage(prefix + locale.msg("event.hpDrain.next", listOf(delay.seconds.toString(), percentage.toString())))
        }
    }

    var counter = delay
    private val task = task(false, 0, 20) {
        if (paused) return@task
        if (counter <= 0) {
            onlinePlayers.forEach { p ->
                val att = p.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return@forEach
                att.baseValue = (att.baseValue - (att.baseValue * (percentage / 100.0))).coerceAtLeast(0.01)
            }
            notifyStatus()
            counter = delay
        }
        counter--
    }
}