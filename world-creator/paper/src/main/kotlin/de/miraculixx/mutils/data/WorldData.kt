package de.miraculixx.mutils.data

import kotlinx.serialization.Serializable
import org.bukkit.World.Environment
import org.bukkit.WorldType

@Serializable
data class WorldData(
    var presetName: String = "MUtils",
    var worldName: String = "new-world",
    var seed: Long? = null,
    var environment: Environment = Environment.NORMAL,
    var generateStructures: Boolean = true,
    var worldType: WorldType = WorldType.NORMAL,
    val biomeProvider: BiomeProviderData = BiomeProviderData(BiomeAlgorithm.VANILLA, BiomeData()),
    val chunkProviders: MutableList<GeneratorData> = mutableListOf(),
    val chunkDefaults: GeneratorDefaults = GeneratorDefaults()
)
