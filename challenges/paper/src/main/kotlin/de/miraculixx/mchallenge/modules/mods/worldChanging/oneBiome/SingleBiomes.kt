package de.miraculixx.mchallenge.modules.mods.worldChanging.oneBiome

import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class SingleBiomes : BiomeProvider() {
    private fun biome(name: String): Biome {
        return when (val s = name.split('-', limit = 2)[0]) {
            else -> {
                try {
                    Biome.valueOf(s)
                } catch (_: Exception) {
                    Biome.PLAINS
                }
            }
        }
    }

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        return biome(worldInfo.name)
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return mutableListOf(biome(worldInfo.name))
    }
}