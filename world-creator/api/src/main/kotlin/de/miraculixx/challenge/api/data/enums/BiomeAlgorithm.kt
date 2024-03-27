package de.miraculixx.challenge.api.data.enums

/**
 * Modify the world biome generation with custom implementations
 */
enum class BiomeAlgorithm(val settings: Map<AlgorithmSettingIndex, AlgorithmSetting>) {
    /**
     * Vanilla biome generation
     */
    VANILLA(emptyMap()),

    /**
     * Every "chunk" is a new random biome. Chunks are defined by the x and z scale
     */
    CHUNKED_BIOMES(mapOf(AlgorithmSettingIndex.X1 to AlgorithmSetting.SCALE_X, AlgorithmSettingIndex.X2 to AlgorithmSetting.SCALE_Z)),

    /**
     * Every block is a new random biome
     */
    RANDOM_BIOMES(emptyMap()),

    /**
     * The hole world is only one biome
     */
    SINGLE_BIOME(mapOf(AlgorithmSettingIndex.KEY to AlgorithmSetting.BIOME)),

//    /**
//     * All biomes are switched with a random one
//     */
//    SWITCHED_BIOMES(mapOf(AlgorithmSettingIndex.RND to AlgorithmSetting.RANDOM));
}