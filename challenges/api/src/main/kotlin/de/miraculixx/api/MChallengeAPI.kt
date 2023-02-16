package de.miraculixx.api

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.ChallengeStatus
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.modules.challenges.StatusChangerAPI
import de.miraculixx.api.settings.ChallengeData
import de.miraculixx.api.settings.challenges
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File

abstract class MChallengeAPI {
    companion object {
        var instance: MChallengeAPI? = null
    }

    abstract val statusChanger: StatusChangerAPI
    var status = ChallengeStatus.STOPPED
    private val activatedChallenges: MutableList<Challenge> = mutableListOf()

    /**
     * @return all activated challenges
     */
    fun getChallenges(): List<Challenge> {
        return activatedChallenges.toList()
    }

    /**
     * Start all activated challenges. If challenges are not stopped, they will be stoped before
     * @return true if startup was successfully, otherwise false (missing challenge requirements, errors, ...)
     */
    fun startChallenges(): Boolean {
        if (status != ChallengeStatus.STOPPED) stopChallenges()
        activatedChallenges.clear()
        activatedChallenges.addAll(statusChanger.startChallenges() ?: return false)
        return true
    }

    /**
     * Stop all running challenges
     * @return false if challenges are already stopped
     */
    fun stopChallenges(): Boolean {
        if (status == ChallengeStatus.STOPPED) return false
        statusChanger.stopChallenges(activatedChallenges)
        return true
    }

    /**
     * Unregister all running challenges. That will stop them from working but saves cached data
     * @return false if challenges are not running
     */
    fun unregisterChallenges(): Boolean {
        if (status != ChallengeStatus.STOPPED) return false
        statusChanger.unregisterChallenges(activatedChallenges)
        return true
    }

    /**
     * Register all paused challenges. That will resume with cached data
     * @return false if challenges are not paused
     */
    fun registerChallenges(): Boolean {
        if (status != ChallengeStatus.PAUSED) return false
        statusChanger.registerChallenges(activatedChallenges)
        return true
    }

    /**
     * Unregister and stop all activated challenges. Mostly used on reloads or server shutdowns.
     *
     * This function should no used for runtime stopping, no attempt will be made to save data. Use [stopChallenges] and [unregisterChallenges] instead!
     */
    fun shutDown() {
        activatedChallenges.forEach {
            if (status != ChallengeStatus.STOPPED) it.stop()
            it.unregister()
        }
    }

    /**
     * Load in all saved data like challenge status and settings
     * @param file The save file
     */
    fun load(file: File) {
        val data = json.decodeFromString<Map<Challenges, ChallengeData>>(file.readJsonString(true))
        data.forEach { (ch, data) ->
            challenges[ch] = data
        }
    }

    /**
     * Saves all data like challenge status and settings. Note: Only **changes** are saved! Default values will be not present in the file.
     * @param file The save file
     */
    fun save(file: File) {
        if (!file.exists()) file.parentFile.mkdirs()
        file.writeText(json.encodeToString(challenges))
    }
}