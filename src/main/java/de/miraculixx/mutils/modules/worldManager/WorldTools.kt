package de.miraculixx.mutils.modules.worldManager

import de.miraculixx.mutils.enums.modules.worldCreator.BiomeProviders
import de.miraculixx.mutils.modules.worldManager.biomeProvider.RandomBiomes
import de.miraculixx.mutils.modules.worldManager.biomeProvider.SingleBiomes
import de.miraculixx.mutils.modules.worldManager.biomeProvider.SwitchBiomes
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleMessage
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import java.nio.file.Paths

class WorldTools {
    private val c: FileConfiguration = ConfigManager.getConfig(Configs.WORLDS)

    fun createWorld(name: String, dim: World.Environment, biomeProvider: BiomeProviders, seed: Long? = null): World? {
        val wc = WorldCreator(name)
        wc.environment(dim)
        when (biomeProvider) {
            BiomeProviders.SINGLE_BIOMES -> wc.biomeProvider(SingleBiomes())
            BiomeProviders.RANDOM_BIOMES -> wc.biomeProvider(RandomBiomes())
            BiomeProviders.BIOME_SWITCH -> wc.biomeProvider(SwitchBiomes())
            BiomeProviders.VANILLA -> {}
        }
        if (seed != null) wc.seed(seed)
        wc.generateStructures(true)
        val world = wc.createWorld()
        if (world != null) {
            val list = c.getStringList("Worlds")
            if (list.contains(name)) return world
            c["World.$name.Generator"] = biomeProvider.name
            c["World.$name.Environment"] = dim.name
            c["World.$name.Seed"] = seed
            c["Worlds"] = list.add(name)
        }
        return world
    }

    fun deleteWorld(world: World?): Boolean {
        if (world == null) return false
        onlinePlayers.forEach {
            if (it.world == world) it.teleport(worlds[0].spawnLocation)
        }
        val name = world.name
        Bukkit.unloadWorld(world, false)
        val currentRelativePath = Paths.get(name)
        val s = currentRelativePath.toAbsolutePath().toString()
        consoleMessage("$prefix Deleting World at $s")
        c["World"] = null
        val list = c.getStringList("Worlds")
        list.remove(name)
        c["Worlds"] = list
        return File(s).deleteRecursively()
    }

    fun loadWorlds() {
        val c = ConfigManager.getConfig(Configs.WORLDS)
        val list = c.getStringList("Worlds")
        if (list.isEmpty()) return
        consoleMessage("$prefix Loading custom worlds...")
        consoleMessage("$prefix The server will use all resources to load worlds quickly, please be patient")
        list.forEach {
            val seed = c.getLong("World.$it.Seed")
            val env = World.Environment.valueOf(c.getString("World.$it.Environment") ?: "NORMAL")
            val gen = BiomeProviders.valueOf(c.getString("World.$it.Generator") ?: "VANILLA")
            createWorld(it, env, gen, seed)
        }
    }
}