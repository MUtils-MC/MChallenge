package de.miraculixx.mutils.data

import kotlinx.serialization.Serializable
import org.bukkit.block.Biome

@Serializable
data class BiomeData(
    var biome: Biome? = null,
    var xScale: Int? = null,
    var zScale: Int? = null,
    var random: Boolean? = null
)

@Serializable
data class BiomeProviderData(
    var algorithm: BiomeAlgorithm,
    var settings: BiomeData,
)

@Serializable
data class GeneratorData(
    val generator: GeneratorAlgorithm,
    val x1: Int? = null,
    val x2: Int? = null,
    val x3: Int? = null,
    val mode: Boolean? = null,
    val rnd: Boolean? = null,
    val invert: Boolean? = null
)

@Serializable
data class GeneratorDefaults(
    val vanillaNoise: Boolean = true,
    val vanillaSurface: Boolean = true,
    val vanillaCaves: Boolean = true,
    val vanillaFoliage: Boolean = true,
    val vanillaStructures: Boolean = true,
    val vanillaMobs: Boolean = true
)