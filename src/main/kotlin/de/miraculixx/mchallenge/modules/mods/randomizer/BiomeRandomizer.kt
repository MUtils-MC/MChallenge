package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.mcommons.text.cError
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix

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