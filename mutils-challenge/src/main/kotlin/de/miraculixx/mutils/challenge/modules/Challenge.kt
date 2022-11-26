package de.miraculixx.mutils.challenge.modules

import de.miraculixx.mutils.challenge.utils.enums.Challenge

interface Challenge {
    val challenge: Challenge
        get() = Challenge.CUSTOM_CHALLENGE

    fun start(): Boolean {
        register()
        return true
    }
    fun stop() {
        unregister()
    }
    fun register()
    fun unregister()
}