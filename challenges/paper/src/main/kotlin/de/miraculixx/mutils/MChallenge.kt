package de.miraculixx.mutils

import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.config.Config
import de.miraculixx.mutils.utils.messages.localization

class MChallenge : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
    }

    override fun load() {
        de.miraculixx.mutils.MChallenge.Companion.INSTANCE = this

        localization = Config(this.javaClass.getResourceAsStream("lang.yml"), "lang")
    }
}