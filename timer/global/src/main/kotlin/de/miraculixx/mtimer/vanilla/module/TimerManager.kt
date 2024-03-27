package de.miraculixx.mtimer.vanilla.module

import de.miraculixx.mtimer.vanilla.data.*
import de.miraculixx.mvanilla.messages.*
import kotlinx.serialization.encodeToString
import java.io.File
import java.util.*

lateinit var rules: Rules
lateinit var goals: Goals
lateinit var settings: Settings

object TimerManager {
    lateinit var globalTimer: Timer
    val personalTimer: MutableMap<UUID, Timer> = mutableMapOf()
    private val designs: MutableMap<UUID, TimerDesign> = mutableMapOf()

    fun getDesign(uuid: UUID): TimerDesign? {
        return designs[uuid]
    }

    fun getDesigns(): Map<UUID, TimerDesign> {
        return designs
    }

    fun addDesign(design: TimerDesign, uuid: UUID) {
        designs[uuid] = design
    }

    fun removeDesign(uuid: UUID, dataFolder: File): Boolean {
        File("${dataFolder.path}/$uuid.json").delete()
        return designs.remove(uuid) != null
    }

    fun getPersonalTimer(uuid: UUID): Timer? {
        return personalTimer[uuid]
    }

    fun addPersonalTimer(uuid: UUID, timer: Timer): Timer {
        personalTimer[uuid] = timer
        return timer
    }

    fun removePersonalTimer(uuid: UUID): Boolean {
        val timer = personalTimer[uuid] ?: return false
        timer.disableTimer()
        return personalTimer.remove(uuid) != null
    }

    private fun toDataObj(timer: Timer): TimerData {
        return TimerData(designs.filter { it.value == timer.design }.keys.firstOrNull() ?: TimerPresets.CLASSIC.uuid, timer.time, timer.visible, timer.countUp, timer.playerID)
    }

    fun save(folder: File) {
        if (debug) consoleAudience.sendMessage(prefix + cmp("Save all data to disk..."))
        val designFolder = File("${folder.path}/designs")
        if (!designFolder.exists()) designFolder.mkdirs()
        val skipIDs = TimerPresets.values().map { it.uuid }
        designs.forEach { (id, data) ->
            if (skipIDs.contains(id)) return@forEach
            File("${designFolder.path}/$id.json").writeText(json.encodeToString(data)) // Obj
        }

        File("${folder.path}/global-timer.json").writeText(json.encodeToString(toDataObj(globalTimer))) // Obj
        File("${folder.path}/personal-timers.json").writeText(json.encodeToString(personalTimer.map { toDataObj(it.value) })) // List
        File("${folder.path}/rules.json").writeText(json.encodeToString(rules))
        File("${folder.path}/goals.json").writeText(json.encodeToString(goals))
    }
}