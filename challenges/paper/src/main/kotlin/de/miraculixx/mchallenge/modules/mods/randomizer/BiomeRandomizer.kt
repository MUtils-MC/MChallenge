package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.mvanilla.messages.cError
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix

class BiomeRandomizer : Challenge {
    override fun register() {
    }

    override fun unregister() {
    }

    override fun start(): Boolean {
        broadcast(prefix + cmp("Biome Randomizer is not compatible in the current version", cError))
        return false
    }
}