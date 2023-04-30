package de.miraculixx.challenge.api.data

import de.miraculixx.challenge.api.data.enums.BiomeAlgorithm
import de.miraculixx.challenge.api.data.enums.GeneratorAlgorithm
import kotlinx.serialization.Serializable

/**
 * Provide a custom biome generation with settings
 */
@Serializable
data class BiomeProviderData(
    var algorithm: BiomeAlgorithm,
    var settings: GeneratorData,
)

/**
 * Provide a custom noise generation with settings
 */
@Serializable
data class GeneratorProviderData(
    var algorithm: GeneratorAlgorithm,
    var settings: GeneratorData
)

/**
 * Provide settings to a custom generator
 */
@Serializable
data class GeneratorData(
    var x1: Int? = null,
    var x2: Int? = null,
    var x3: Int? = null,
    var mode: Boolean? = null,
    var rnd: Boolean? = null,
    var invert: Boolean? = null,
    var key: String? = null
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