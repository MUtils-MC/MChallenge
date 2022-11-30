package de.miraculixx.mutils.challenge

import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.utils.config.Config
import de.miraculixx.mutils.utils.messages.localization

class MChallenge : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
    }

    override fun load() {
        INSTANCE = this

        localization = Config(this.javaClass.getResourceAsStream("lang.yml"), "lang")
    }
}