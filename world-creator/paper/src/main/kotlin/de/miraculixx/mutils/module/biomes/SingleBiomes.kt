package de.miraculixx.mutils.module.biomes

import de.miraculixx.mutils.data.BiomeData
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class SingleBiomes(biomeData: BiomeData) : BiomeProvider() {
    private val biome = biomeData.biome?.let {
        try { Biome.valueOf(it) } catch (_: IllegalArgumentException) { Biome.PLAINS }
    } ?: Biome.PLAINS

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        return biome
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return mutableListOf(biome)
    }
}