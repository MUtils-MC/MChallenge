package de.miraculixx.mtimer.vanilla.module

import de.miraculixx.timer.api.MTimerAPI
import java.util.*
import kotlin.time.Duration

abstract class TimerAPIImplementation : MTimerAPI() {
    override fun getTimerStatus(): Boolean {
        return TimerManager.globalTimer.running
    }

    override fun getTimerStatus(uuid: UUID): Boolean {
        return TimerManager.getPersonalTimer(uuid)?.running ?: false
    }

    override fun startTimer(): Boolean {
        val timer = TimerManager.globalTimer
        if (timer.running) return false
        timer.running = true
        return true
    }

    override fun stopTimer(): Boolean {
        val timer = TimerManager.globalTimer
        if (!timer.running) return false
        timer.running = false
        return true
    }

    override fun setTime(duration: Duration) {
        val timer = TimerManager.globalTimer
        timer.time = duration
    }

    override fun setTime(uuid: UUID, duration: Duration): Boolean {
        val timer = TimerManager.getPersonalTimer(uuid) ?: return false
        timer.time = duration
        return true
    }

    override fun addTickLogic(onTick: (Duration) -> Unit) {
        TimerManager.globalTimer.addTickLogic(onTick)
    }

    override fun onStopLogic(onStop: () -> Unit) {
        TimerManager.globalTimer.addStopLogic(onStop)
    }

    override fun onStartLogic(onStart: () -> Unit) {
        TimerManager.globalTimer.addStartLogic(onStart)
    }
}