package de.miraculixx.mchallenge.modules.mods.mineField

import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class MineFieldWorld: ChunkGenerator() {
    fun createWorld() =  WorldCreator.name(UUID.randomUUID().toString()).generator(this).createWorld()

    override fun shouldGenerateCaves() = true
    override fun shouldGenerateNoise() = true
    override fun shouldGenerateSurface() = true

    override fun generateCaves(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        (0..15).forEach { x ->
            (0..15).forEach { z ->
                val realX = (chunkX * 16) + x
                val realZ = (chunkZ * 16) + z
                (chunkData.minHeight until  chunkData.maxHeight).forEach { y ->
                    val currentBlock = chunkData.getBlockData(x, y, z)
                    println("$x $y $z" + currentBlock.asString)
                    if (currentBlock.material.isAir) chunkData.setBlock(x, y, z, Material.SPONGE)
                }
            }
        }
    }
}