package de.miraculixx.mutils.modules.challenge

import de.miraculixx.mutils.enums.modules.Modules

interface Challenge {
    val challenge: Modules
        get() = Modules.CUSTOM_CHALLENGE

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