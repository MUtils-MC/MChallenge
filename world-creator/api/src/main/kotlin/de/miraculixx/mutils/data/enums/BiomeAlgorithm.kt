package de.miraculixx.mutils.data.enums

/**
 * Modify the world biome generation with custom implementations
 */
enum class BiomeAlgorithm {
    /**
     * Vanilla biome generation
     */
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
}