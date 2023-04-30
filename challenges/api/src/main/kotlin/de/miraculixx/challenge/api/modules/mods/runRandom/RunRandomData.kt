package de.miraculixx.challenge.api.modules.mods.runRandom

import net.kyori.adventure.bossbar.BossBar


data class RunRandomData(
    var distance: Double,
    val bar: BossBar
)