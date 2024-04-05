package de.miraculixx.mutils.data

import de.miraculixx.challenge.api.data.GeneratorData
import de.miraculixx.challenge.api.data.enums.AlgorithmSetting
import de.miraculixx.challenge.api.data.enums.BiomeAlgorithm
import de.miraculixx.challenge.api.data.enums.Dimension
import de.miraculixx.mutils.module.biomes.ChunkBiomes
import de.miraculixx.mutils.module.biomes.RandomBiomes
import de.miraculixx.mutils.module.biomes.SingleBiomes
import de.miraculixx.mutils.module.biomes.SwitchBiomes
import org.bukkit.Material
import org.bukkit.World.Environment
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator

data class ChunkCalcData(val chunkX: Int, val chunkZ: Int, val chunkData: ChunkGenerator.ChunkData)

fun BiomeAlgorithm.getProvider(biomeData: GeneratorData): BiomeProvider? {
    return when (this) {
        BiomeAlgorithm.VANILLA -> null
        BiomeAlgorithm.CHUNKED_BIOMES -> ChunkBiomes(biomeData)
        BiomeAlgorithm.RANDOM_BIOMES -> RandomBiomes(biomeData)
        BiomeAlgorithm.SINGLE_BIOME -> SingleBiomes(biomeData)
//        BiomeAlgorithm.SWITCHED_BIOMES -> SwitchBiomes(biomeData)
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
        AlgorithmSetting.BIOME -> Material.ACACIA_SAPLING
        AlgorithmSetting.HEIGHT -> Material.HOPPER
    }
}

fun Environment.toDimension(): Dimension {
    return when (this) {
        Environment.NORMAL, Environment.CUSTOM -> Dimension.NORMAL
        Environment.NETHER -> Dimension.NETHER
        Environment.THE_END -> Dimension.THE_END
    }
}