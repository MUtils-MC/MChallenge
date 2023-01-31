package de.miraculixx.mutils.data

import de.miraculixx.mutils.data.enums.BiomeAlgorithm
import de.miraculixx.mutils.data.enums.Dimension
import de.miraculixx.mutils.data.enums.VanillaGenerator
import kotlinx.serialization.Serializable

/**
 * Custom World Data
 *
 * From this object new worlds will be generated and saved.
 * @param presetName Name that will be visual in global library
 * @param worldName World Name
 * @param category Custom category for separate play data. Vanilla is synced with the three default worlds
 * @param seed World Seed
 * @param environment World Dimension
 * @param worldType Vanilla generation modifier - Like flat or amplified
 * @param biomeProvider Custom biome provider
 * @param chunkProviders Custom noise providers. Render order is -> Vanilla - Entry 1 - Entry 2 - ...
 * @param chunkDefaults Vanilla noise modifications
 */
@Serializable
data class WorldData(
    var presetName: String = "MUtils",
    var worldName: String = "error",
    var category: String = "Vanilla",
    var seed: Long? = null,
    var environment: Dimension = Dimension.NORMAL,
    var worldType: VanillaGenerator = VanillaGenerator.NORMAL,
    val biomeProvider: BiomeProviderData = BiomeProviderData(BiomeAlgorithm.VANILLA, GeneratorData()),
    val chunkProviders: MutableList<GeneratorProviderData> = mutableListOf(),
    val chunkDefaults: GeneratorDefaults = GeneratorDefaults()
)
