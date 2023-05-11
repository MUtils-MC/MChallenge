package de.miraculixx.mtimer.module

import de.miraculixx.mtimer.data.Rules
import de.miraculixx.mtimer.data.TimerData
import de.miraculixx.mtimer.data.TimerPresets
import de.miraculixx.mtimer.server
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.extensions.toUUID
import de.miraculixx.mvanilla.messages.*
import kotlinx.serialization.decodeFromString
import java.io.File
import kotlin.time.Duration

var globalTimerState = false

fun pauseGlobalTimer() {
    globalTimerState = false
}

fun resumeGlobalTimer() {
    globalTimerState = true
}

fun TimerManager.load(folder: File) {
    if (debug) consoleAudience.sendMessage(prefix + cmp("Load all data from disk..."))
    val designsFolder = File("${folder.path}/designs")
    if (!designsFolder.exists()) designsFolder.mkdirs()
    TimerPresets.values().forEach {
        if (it == TimerPresets.PRESET) return@forEach
        addDesign(it.design, it.uuid)
    }
    designsFolder.listFiles()?.forEach { file ->
        if (file.extension != "json") return@forEach
        val fileOut = file.readJsonString(true)
        addDesign(
            try {
                json.decodeFromString(fileOut)
            } catch (e: Exception) {
                consoleAudience.sendMessage(prefix + cmp("Invalid file: ${e.message}", cError))
                return@forEach
            }, file.nameWithoutExtension.toUUID() ?: return@forEach
        )
    }

    globalTimer = try {
        resolveTimer(json.decodeFromString(File("${folder.path}/global-timer.json").readJsonString(true)))
    } catch (e: Exception) {
        if (debug) consoleAudience.sendMessage(prefix + cmp("Malformed global timer save file! Creating a default timer..."))
        resolveTimer(TimerData(TimerPresets.CLASSIC.uuid, Duration.ZERO, true, true))
    }


    val pTimerOut = json.decodeFromString<List<TimerData>>(File("${folder.path}/personal-timers.json").readJsonString(false))
    personalTimer.forEach { (_, timer) -> timer.disableTimer() }
    personalTimer.clear()
    pTimerOut.forEach { pt -> pt.playerUUID?.let { personalTimer[it] = resolveTimer(pt) } }
    de.miraculixx.mtimer.vanilla.module.rules = json.decodeFromString(File("${folder.path}/rules.json").readJsonString(true))
    de.miraculixx.mtimer.vanilla.module.goals = json.decodeFromString(File("${folder.path}/goals.json").readJsonString(true))

    if (!globalTimer.running) {
        globalTimer.running = false
    }
}

private fun resolveTimer(data: TimerData): FabricTimer {
    val timer = FabricTimer(data.playerUUID != null, data.playerUUID, data.timerDesign, data.isVisible, server.playerList)
    timer.design = TimerManager.getDesign(data.timerDesign) ?: TimerManager.getDesign(TimerPresets.CLASSIC.uuid) ?: TimerPresets.error
    timer.time = data.time
    return timer
}