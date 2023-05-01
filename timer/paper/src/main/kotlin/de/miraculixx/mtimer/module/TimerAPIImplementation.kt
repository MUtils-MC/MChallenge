package de.miraculixx.mtimer.module

import de.miraculixx.mtimer.data.TimerPresets
import de.miraculixx.timer.api.MTimerAPI
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.Duration

object TimerAPIImplementation : MTimerAPI() {
    init {
        instance = this
    }

    override fun getTimerStatus(): Boolean {
        return TimerManager.getGlobalTimer().running
    }

    override fun getTimerStatus(uuid: UUID): Boolean {
        return TimerManager.getPersonalTimer(uuid)?.running ?: false
    }

    override fun startTimer(): Boolean {
        val timer = TimerManager.getGlobalTimer()
        if (timer.running) return false
        timer.running = true
        return true
    }

    override fun startTimer(uuid: UUID): Boolean {
        val timer = getPersonalTimer(uuid)
        if (timer.running) return false
        timer.running = true
        return true
    }

    override fun stopTimer(): Boolean {
        val timer = TimerManager.getGlobalTimer()
        if (!timer.running) return false
        timer.running = false
        return true
    }

    override fun stopTimer(uuid: UUID): Boolean {
        val timer = getPersonalTimer(uuid)
        if (!timer.running) return false
        timer.running = false
        return true
    }

    override fun addTickLogic(onTick: (Duration) -> Unit) {
        TimerManager.getGlobalTimer().addTickLogic(onTick)
    }

    override fun onStopLogic(onStop: () -> Unit) {
        TimerManager.getGlobalTimer().addStopLogic(onStop)
    }

    override fun onStartLogic(onStart: () -> Unit) {
        TimerManager.getGlobalTimer().addStartLogic(onStart)
    }

    private fun getPersonalTimer(uuid: UUID): Timer {
        return TimerManager.getPersonalTimer(uuid) ?: TimerManager.addPersonalTimer(uuid, Timer(true, Bukkit.getOfflinePlayer(uuid), TimerPresets.CLASSIC.uuid, true))
    }
}