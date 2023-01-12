package de.miraculixx.mutils.module

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.mutils.MTimer
import de.miraculixx.mutils.data.TimerData
import de.miraculixx.mutils.data.TimerDesign
import de.miraculixx.mutils.data.TimerPresets
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.extensions.toUUID
import de.miraculixx.mutils.messages.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.Bukkit
import java.io.File
import java.util.*
import kotlin.time.Duration

object TimerManager {
    private lateinit var globalTimer: Timer
    private val personalTimer: MutableMap<UUID, Timer> = mutableMapOf()
    private val designs: MutableMap<UUID, TimerDesign> = mutableMapOf()
    private val dataFolder = File("${MTimer.configFolder.path}/designs")

    fun getDesign(uuid: UUID): TimerDesign? {
        return designs[uuid]
    }

    fun getDesigns(): Map<UUID, TimerDesign> {
        return designs
    }

    fun addDesign(design: TimerDesign, uuid: UUID) {
        designs[uuid] = design
    }

    fun removeDesign(uuid: UUID): Boolean {
        File("${dataFolder.path}/$uuid.json").delete()
        return designs.remove(uuid) != null
    }

    fun getGlobalTimer(): Timer {
        return globalTimer
    }

    fun getPersonalTimer(uuid: UUID): Timer? {
        return personalTimer[uuid]
    }

    fun addPersonalTimer(uuid: UUID, timer: Timer) {
        personalTimer[uuid] = timer
    }

    fun removePersonalTimer(uuid: UUID): Boolean {
        val timer = personalTimer[uuid] ?: return false
        timer.disableTimer()
        return personalTimer.remove(uuid) != null
    }

    private fun resolveTimer(data: TimerData): Timer {
        val timer = Timer(data.playerUUID != null, data.playerUUID?.let { Bukkit.getOfflinePlayer(it) })
        timer.design = getDesign(data.timerDesign) ?: getDesign(TimerPresets.CLASSIC.uuid) ?: TimerPresets.error
        timer.setTime(data.time)
        return timer
    }

    private fun toDataObj(timer: Timer): TimerData {
        return TimerData(designs.filter { it.value == timer.design }.keys.firstOrNull() ?: TimerPresets.CLASSIC.uuid, timer.getTime(), timer.visible, timer.countUp, timer.player?.uniqueId)
    }

    fun save(folder: File) {
        if (debug) console.sendMessage(prefix + cmp("Save all data to disk..."))
        val designFolder = File("${folder.path}/designs")
        if (!designFolder.exists()) designFolder.mkdirs()
        val skipIDs = TimerPresets.values().map { it.uuid }
        designs.forEach { (id, data) ->
            if (skipIDs.contains(id)) return@forEach
            File("${designFolder.path}/$id.json").writeText(json.encodeToString(data)) // Obj
        }

        File("${folder.path}/global-timer.json").writeText(json.encodeToString(toDataObj(globalTimer))) // Obj
        File("${folder.path}/personal-timers.json").writeText(json.encodeToString(personalTimer.map { toDataObj(it.value) })) // List
    }

    fun load(folder: File) {
        if (debug) console.sendMessage(prefix + cmp("Load all data from disk..."))
        val designsFolder = File("${folder.path}/designs")
        if (!designsFolder.exists()) designsFolder.mkdirs()
        TimerPresets.values().forEach {
            if (it == TimerPresets.PRESET) return@forEach
            designs[it.uuid] = it.design
        }
        designsFolder.listFiles()?.forEach { file ->
            if (file.extension != "json") return@forEach
            val fileOut = file.readJsonString(true)
            designs[file.nameWithoutExtension.toUUID() ?: return@forEach] = try {
                json.decodeFromString(fileOut)
            } catch (e: Exception) {
                console.sendMessage(prefix + cmp("Invalid file: ${e.message}", cError))
                return@forEach
            }
        }

        globalTimer = try {
            resolveTimer(json.decodeFromString(File("${folder.path}/global-timer.json").readJsonString(true)))
        } catch (e: Exception) {
            if (debug) console.sendMessage(prefix + cmp("Malformed global timer save file! Creating a default timer..."))
            resolveTimer(TimerData(TimerPresets.CLASSIC.uuid, Duration.ZERO, true, true))
        }

        val pTimerOut = json.decodeFromString<List<TimerData>>(File("${folder.path}/personal-timers.json").readJsonString(false))
        personalTimer.forEach { (_, timer) -> timer.disableTimer() }
        personalTimer.clear()
        pTimerOut.forEach { pt -> pt.playerUUID?.let { personalTimer[it] = resolveTimer(pt) } }
    }
}