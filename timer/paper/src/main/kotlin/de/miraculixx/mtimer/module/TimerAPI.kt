package de.miraculixx.mtimer.module

import de.miraculixx.mtimer.vanilla.data.TimerPresets
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerAPIImplementation
import de.miraculixx.mtimer.vanilla.module.TimerManager
import java.util.*

object TimerAPI : TimerAPIImplementation() {
    override fun startTimer(uuid: UUID): Boolean {
        val timer = getPersonalTimer(uuid)
        if (timer.running) return false
        timer.running = true
        return true
    }

    override fun stopTimer(uuid: UUID): Boolean {
        val timer = getPersonalTimer(uuid)
        if (!timer.running) return false
        timer.running = false
        return true
    }

    private fun getPersonalTimer(uuid: UUID): Timer {
        return TimerManager.getPersonalTimer(uuid) ?: TimerManager.addPersonalTimer(uuid, PaperTimer(true, uuid, TimerPresets.CLASSIC.uuid))
    }
}