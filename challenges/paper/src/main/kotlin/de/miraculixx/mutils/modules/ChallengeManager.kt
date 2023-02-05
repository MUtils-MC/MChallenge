package de.miraculixx.mutils.modules

import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.enums.challenges.ChallengeStatus
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.json
import de.miraculixx.mutils.utils.settings
import de.miraculixx.mutils.utils.settings.ChallengeData
import de.miraculixx.mutils.utils.settings.challenges
import kotlinx.serialization.decodeFromString
import java.io.File

/*
Global quick access
 */


object ChallengeManager {
    var status = ChallengeStatus.STOPPED
    val activatedChallenges = ArrayList<Challenge>()

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
        val manager = StatusChanger()
        if (status != ChallengeStatus.STOPPED) stopChallenges()
        activatedChallenges.clear()
        activatedChallenges.addAll(manager.startChallenges() ?: return false)
        return true
    }

    /**
     * Stop all running challenges
     * @return false if challenges are already stopped
     */
    fun stopChallenges(): Boolean {
        if (status == ChallengeStatus.STOPPED) return false
        val manager = StatusChanger()
        manager.stopChallenges(activatedChallenges)
        return true
    }

    /**
     * Unregister all running challenges. That will stop them from working but save cached data
     * @return false if challenges are not running
     */
    fun unregisterChallenges(): Boolean {
        if (status != ChallengeStatus.STOPPED) return false
        val manager = StatusChanger()
        manager.unregisterChallenges(activatedChallenges)
        return true
    }

    /**
     * Register all paused challenges. That will resume with cached data
     * @return false if challenges are not paused
     */
    fun registerChallenges(): Boolean {
        if (status != ChallengeStatus.PAUSED) return false
        val manager = StatusChanger()
        manager.registerChallenges(activatedChallenges)
        return true
    }

    fun isActive(module: Challenges): Boolean {
        return settings.getBoolean("${module.name}.active")
    }

    fun shutDown() {
        activatedChallenges.forEach {
            if (status != ChallengeStatus.STOPPED) it.stop()
            it.unregister()
        }
    }

    fun load(file: File) {
        val data = json.decodeFromString<Map<Challenges, ChallengeData>>(file.readJsonString(false))
        data.forEach { (ch, data) ->
            challenges[ch] = data
        }
    }
}