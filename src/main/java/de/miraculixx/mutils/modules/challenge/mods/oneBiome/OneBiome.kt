package de.miraculixx.mutils.modules.challenge.mods.oneBiome

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.worldCreator.BiomeProviders
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.worldManager.WorldTools
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.player.PlayerPortalEvent

class OneBiome: Challenge() {
    override val challenge = Modules.ONE_BIOME
    private var switcher: OneBiomeSwitcher? = null

    override fun start(): Boolean {
        switcher = OneBiomeSwitcher(ConfigManager.getConfig(Configs.MODULES))
        return true
    }

    override fun stop() {
        switcher?.stop()
        switcher = null
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
            val manager = WorldTools()
            it.to.world = manager.createWorld(sEnv,env,BiomeProviders.SINGLE_BIOMES,it.from.world.seed)?: return@listen
        }
        it.to.world = dWorld
    }
}