package de.miraculixx.mutils.module.biomes

import de.miraculixx.api.data.GeneratorData
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class SingleBiomes(biomeData: GeneratorData) : BiomeProvider() {
    private val biome = biomeData.key?.let {
        try {
            Biome.valueOf(it)
        } catch (_: IllegalArgumentException) {
            Biome.PLAINS
        }
    } ?: Biome.PLAINS

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        return biome
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return mutableListOf(biome)
    }
}