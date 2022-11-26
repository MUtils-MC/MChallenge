package de.miraculixx.mutils.challenge.modules

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.utils.enums.challenges.ChallengeStatus

/*
Global quick access
 */


object ChallengeManager {
    var status = ChallengeStatus.STOPPED
    private val activatedChallenges = ArrayList<Challenge>()

    /**
     * @return all activated challenges
     */
    fun getChallenges(): List<Challenge> {
        return buildList {
            addAll(activatedChallenges)
        }
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

    /**
     * Add a challenge to the activation list.
     * @return false if challenge is already activated
     */
    private fun addChallenge(challenge: Challenge): Boolean {
        return if (activatedChallenges.contains(challenge)) false
        else {
            activatedChallenges.add(challenge)
            true
        }
    }

    /**
     * Remove a challenge from the activation list.
     * @return false if challenge is not activated
     */
    private fun removeChallenge(challenge: Challenge): Boolean {
        return if (activatedChallenges.contains(challenge)) {
            activatedChallenges.remove(challenge)
            true
        } else false
    }


    fun isActive(module: Challenge): Boolean {
        return activatedChallenges.contains(module)
    }


    /*
    Config managing
    Load data from Disk or save it back
     */
    private fun load() {
        val c = ConfigManager.getConfig(Configs.MODULES)
        Challenge.values().forEach { s: Challenge ->
            val active = c.getBoolean("${s.name}.Active")
            moduleMap[s] = active
            if (active) enableModule(s)
        }
        taskRunLater(20) {
            val wTools = WorldTools()
            wTools.loadWorlds()
        }
    }

    fun save() {
        val c = ConfigManager.getConfig(Configs.MODULES)
        moduleMap.forEach { (s, b) ->
            c["${s.name}.Active"] = b
        }
        val cT = ConfigManager.getConfig(Configs.TIMER)
        cT["Time.Seconds"] = timer.getTime(TimerValue.SECONDS)
        cT["Time.Minutes"] = timer.getTime(TimerValue.MINUTES)
        cT["Time.Hours"] = timer.getTime(TimerValue.HOURS)
        cT["Time.Days"] = timer.getTime(TimerValue.DAYS)
    }

    fun shutDown() {
        activatedChallenges.forEach {
            if (challenges != ChallengeStatus.STOPPED) it.stop()
            it.unregister()
        }
        timer.setActive(false)
    }

    init {
        load()
    }
}