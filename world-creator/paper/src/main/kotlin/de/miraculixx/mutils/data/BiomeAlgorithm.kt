package de.miraculixx.mutils.data

import de.miraculixx.mutils.module.biomes.ChunkBiomes
import de.miraculixx.mutils.module.biomes.RandomBiomes
import de.miraculixx.mutils.module.biomes.SingleBiomes
import de.miraculixx.mutils.module.biomes.SwitchBiomes
import org.bukkit.generator.BiomeProvider

enum class BiomeAlgorithm {
    VANILLA,

    /**
     * Every chunk is a new random biome
     */
    CHUNKED_BIOMES,

    /**
     * Every block is a new random biome
     */
    RANDOM_BIOMES,

    /**
     * The hole world is only one biome
     */
    SINGLE_BIOME,

    /**
     * All biomes are switched with a random one
     */
    SWITCHED_BIOMES;

    fun getProvider(biomeData: BiomeData): BiomeProvider? {
        return when (this) {
            VANILLA -> null
            CHUNKED_BIOMES -> ChunkBiomes(biomeData)
            RANDOM_BIOMES -> RandomBiomes(biomeData)
            SINGLE_BIOME -> SingleBiomes(biomeData)
            SWITCHED_BIOMES -> SwitchBiomes(biomeData)
        }
    }
}