package de.miraculixx.challenge.addon

import de.miraculixx.kpaper.main.KSpigot

class MAddon : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        AddonManager.loadChallenges()
    }

    override fun shutdown() {
        AddonManager.saveChallenges()
    }
}

val PluginInstance by lazy { MAddon.INSTANCE }