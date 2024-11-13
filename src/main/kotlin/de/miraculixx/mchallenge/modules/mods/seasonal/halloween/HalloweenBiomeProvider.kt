package de.miraculixx.mchallenge.modules.mods.seasonal.halloween

import org.bukkit.FeatureFlag
import org.bukkit.Registry
import org.bukkit.World.Environment
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.jetbrains.annotations.Unmodifiable
import java.util.*

class TestChunkGenerator: ChunkGenerator() {

}

class HalloweenBiomeProvider : BiomeProvider() {
    private lateinit var internalWorldInfo: InternalWorldInfo

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        internalWorldInfo = InternalWorldInfo(worldInfo.name, worldInfo.uid, worldInfo.environment, worldInfo.seed, worldInfo.minHeight..worldInfo.maxHeight)
        return Registry.BIOME.toMutableList()
    }

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        // Used to get vanilla biome at position
        val vanillaProvider = worldInfo.vanillaBiomeProvider().getBiome(internalWorldInfo, x, y, z)
        return Biome.PLAINS
    }

    private class InternalWorldInfo(
        val worldName: String,
        val uuid: UUID,
        val env: Environment,
        val worldSeed: Long,
        val height: IntRange
    ) : WorldInfo {
        override fun getName() = worldName

        override fun getUID() = uuid

        override fun getEnvironment() = env

        override fun getSeed() = worldSeed

        override fun getMinHeight() = height.first

        override fun getMaxHeight() = height.last

        override fun vanillaBiomeProvider(): BiomeProvider {
            throw NotImplementedError("The internal world info is only for using inside a vanilla biome provider, not to work with it!")
        }

        override fun getFeatureFlags(): @Unmodifiable Set<FeatureFlag?> {
            return setOf()
        }
    }
}