package de.miraculixx.mchallenge.modules.mods.oneBiome

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.event.player.PlayerPortalEvent

class OneBiome : Challenge {
    private val switcher = OneBiomeSwitcher()

    override fun stop() {
        switcher.stop()
    }

    override fun register() {
        onPortal.register()
    }

    override fun unregister() {
        onPortal.unregister()
    }

    private val onPortal = listen<PlayerPortalEvent>(register = false) {
        val biome = it.from.world.name.split('-', limit = 2)[0]
        val env = it.to.world.environment
        val sEnv = when (env) {
            World.Environment.NORMAL -> "O"
            World.Environment.NETHER -> "N"
            World.Environment.THE_END -> "E"
            else -> return@listen
        }
        val dWorld = Bukkit.getWorld("$biome-$sEnv")
        if (dWorld == null) {
            it.to.world = WorldCreator(sEnv).environment(env).biomeProvider(SingleBiomes()).seed(it.from.world.seed).createWorld() ?: return@listen
        }
        it.to.world = dWorld
    }
}