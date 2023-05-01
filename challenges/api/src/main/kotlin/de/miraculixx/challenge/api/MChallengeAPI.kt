package de.miraculixx.challenge.api

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.ChallengeStatus
import de.miraculixx.challenge.api.modules.challenges.CustomChallengeData
import java.util.*

abstract class MChallengeAPI {
    companion object {
        var instance: MChallengeAPI? = null
    }

    /**
     * @return all activated challenges
     */
    abstract fun getChallenges(): List<Challenge>

    /**
     * Start all activated challenges. If challenges are not stopped, they will be stoped before
     * @return true if startup was successfully, otherwise false (missing challenge requirements, errors, ...)
     */
    abstract fun startChallenges(): Boolean

    /**
     * Stop all running challenges
     * @return false if challenges are already stopped
     */
    abstract fun stopChallenges(): Boolean

    /**
     * Unregister all running challenges. That will stop them from working but saves cached data
     * @return false if challenges are not running
     */
    abstract fun pauseChallenges(): Boolean

    /**
     * Register all paused challenges. That will resume with cached data
     * @return false if challenges are not paused
     */
    abstract fun resumeChallenges(): Boolean

    /**
     * @return The current challenge status. Challenges can be stopped, running and paused (not running but all data is saved to continue later)
     */
    abstract fun getChallengeStatus(): ChallengeStatus

    /**
     * Add a new Challenge handled by your addon to MUtils-Challenges. 3rd party challenges are marked as addons and must be loaded on each startup.
     * All persistent data like challenge settings must be saved by the addon.
     * @param key The unique key to modify later
     * @param data All challenge data. This object can **not** be modified later
     * @return The final challenge data object. It can be used to modify the challenge data
     */
    abstract fun addChallenge(key: UUID, data: CustomChallengeData): CustomChallengeData?

    /**
     * Remove a custom Challenge by their [UUID]
     * @param key The unique Challenge key
     * @return False if no challenge exist with given key
     */
    abstract fun removeChallenge(key: UUID): Boolean

    /**
     * @return The custom Challenge data by the given key. Null if no Challenge exist
     */
    abstract fun getChallenge(key: UUID): CustomChallengeData?
}