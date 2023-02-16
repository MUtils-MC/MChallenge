package de.miraculixx.mutils.module.biomes

import de.miraculixx.api.data.GeneratorData
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo
import kotlin.random.Random

class RandomBiomes(biomeData: GeneratorData) : BiomeProvider() {
    private val biomeList = Biome.values().filter { it != Biome.CUSTOM }
    private var random: Random? = null

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        return biomeList.random(getRandom(worldInfo.seed))
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return biomeList.toMutableList()
    }

    private fun getRandom(seed: Long): Random {
        return if (random == null) {
            val new = Random(seed)
            random = new
            new
        } else random!!
    }
}