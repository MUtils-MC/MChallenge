package de.miraculixx.challenge.api.modules.challenges

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