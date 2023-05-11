package de.miraculixx.challenge.api.modules.challenges

/**
 * Represents all mods.
 *
 * - [start] - Only called once on "/challenge start"
 * - [stop] - Only called once on "/challenge stop" or plugin shutdown
 * - [register] - Called once on "/challenge start" and on every resume with "/challenge resume"
 * - [unregister] - Called once on "/challenge stop" and on every pause with "/challenge pause"
 */
interface Challenge {
    /**
     * Any start logic that is run *before* the challenge is registered. Return false to abort start up
     */
    fun start(): Boolean {
        return true
    }

    /**
     * Any stop logic that is run *after* the challenge is unregistered
     */
    fun stop() {}

    /**
     * Activate the Challenge. Used to enable all listeners
     */
    fun register()

    /**
     * Deactivate the Challenge. Used to disable all listeners
     */
    fun unregister()
}