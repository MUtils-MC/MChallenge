package de.miraculixx.mutils.data

import de.miraculixx.mutils.data.enums.AlgorithmSetting
import de.miraculixx.mutils.data.enums.BiomeAlgorithm
import de.miraculixx.mutils.module.biomes.ChunkBiomes
import de.miraculixx.mutils.module.biomes.RandomBiomes
import de.miraculixx.mutils.module.biomes.SingleBiomes
import de.miraculixx.mutils.module.biomes.SwitchBiomes
import org.bukkit.Material
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator

data class ChunkCalcData(val chunkX: Int, val chunkZ: Int, val chunkData: ChunkGenerator.ChunkData)

fun BiomeAlgorithm.getProvider(biomeData: BiomeData): BiomeProvider? {
    return when (this) {
        BiomeAlgorithm.VANILLA -> null
        BiomeAlgorithm.CHUNKED_BIOMES -> ChunkBiomes(biomeData)
        BiomeAlgorithm.RANDOM_BIOMES -> RandomBiomes(biomeData)
        BiomeAlgorithm.SINGLE_BIOME -> SingleBiomes(biomeData)
        BiomeAlgorithm.SWITCHED_BIOMES -> SwitchBiomes(biomeData)
    }
}

fun AlgorithmSetting.getIcon(): Material {
    return when (this) {
        AlgorithmSetting.X_DIRECTION -> Material.SPECTRAL_ARROW
        AlgorithmSetting.INVERTED -> Material.ENDER_EYE
        AlgorithmSetting.RANDOM -> Material.DROPPER
        AlgorithmSetting.SOLID_THICKNESS -> Material.LIME_CONCRETE_POWDER
        AlgorithmSetting.HOLE_THICKNESS -> Material.BLACK_CONCRETE_POWDER
        AlgorithmSetting.SCALE_X -> Material.GOLD_NUGGET
        AlgorithmSetting.SCALE_Z -> Material.GOLD_INGOT
    }
}
