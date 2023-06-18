package de.miraculixx.mutils.module.biomes

import de.miraculixx.challenge.api.data.GeneratorData
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class ChunkBiomes(biomeData: GeneratorData) : BiomeProvider() {
    private val biomeList = Biome.values().filter { it != Biome.CUSTOM }.shuffled()
    private val chunkMap = mutableMapOf<String, Biome>()
    private val xScale = biomeData.x1 ?: 1
    private val zScale = biomeData.x2 ?: 1

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        val key = "${x / xScale}-${z / zScale}"
        val biome = chunkMap[key]
        return if (biome == null) {
            val new = biomeList.random()
            chunkMap[key] = new
            new
        } else biome
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return biomeList.toMutableList()
    }
}