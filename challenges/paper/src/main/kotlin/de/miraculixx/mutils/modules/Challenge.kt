package de.miraculixx.mutils.modules

import de.miraculixx.mutils.enums.Challenges

interface Challenge {
    val challenge: Challenges

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

    fun register()
    fun unregister()
}