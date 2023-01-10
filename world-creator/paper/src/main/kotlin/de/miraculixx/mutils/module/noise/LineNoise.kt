package de.miraculixx.mutils.module.noise

import de.miraculixx.mutils.data.BiomeData
import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class LineNoise(private val maxHeight: Int, private val minHeight: Int, private val xScale: Int, private val zScale: Int): ChunkGenerator() {
    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        chunkData.setRegion(1, -64, 1, 15, 319, 15, Material.AIR)
    }

    override fun shouldGenerateNoise(): Boolean { return true }
    override fun shouldGenerateSurface(): Boolean { return true }
    override fun shouldGenerateCaves(): Boolean { return true }
    override fun shouldGenerateDecorations(): Boolean { return true }
    override fun shouldGenerateMobs(): Boolean { return true }
    override fun shouldGenerateStructures(): Boolean { return true }
}