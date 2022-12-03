package de.miraculixx.mutils.modules

import de.miraculixx.mutils.utils.enums.Challenge

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