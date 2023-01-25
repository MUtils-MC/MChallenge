package de.miraculixx.mutils.data

import de.miraculixx.mutils.data.enums.BiomeAlgorithm
import de.miraculixx.mutils.data.enums.GeneratorAlgorithm
import kotlinx.serialization.Serializable

/**
 * Biome data to customize custom biome generation. Not all values are affective on all generators
 */
@Serializable
data class BiomeData(
    var biome: String? = null,
    var xScale: Int? = null,
    var zScale: Int? = null,
    var random: Boolean? = null
)

/**
 * Provide a custom biome generation with settings
 */
@Serializable
data class BiomeProviderData(
    var algorithm: BiomeAlgorithm,
    var settings: BiomeData,
)

/**
 * Provide a custom noise generation with settings
 */
@Serializable
data class GeneratorData(
    val generator: GeneratorAlgorithm,
    var x1: Int? = null,
    var x2: Int? = null,
    var x3: Int? = null,
    var mode: Boolean? = null,
    var rnd: Boolean? = null,
    var invert: Boolean? = null
)

/**
 * Change vanilla generation behaviour. This render on first step, every other customization is applied afterwards
 */
@Serializable
data class GeneratorDefaults(
    var vanillaNoise: Boolean = true,
    var vanillaSurface: Boolean = true,
    var vanillaCaves: Boolean = true,
    var vanillaFoliage: Boolean = true,
    var vanillaStructures: Boolean = true,
    var vanillaMobs: Boolean = true
)