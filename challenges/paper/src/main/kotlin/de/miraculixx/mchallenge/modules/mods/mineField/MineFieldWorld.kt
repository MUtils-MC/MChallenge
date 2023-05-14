package de.miraculixx.mchallenge.modules.mods.mineField

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class MineFieldWorld {
    fun createWorld() =  WorldCreator.name(UUID.randomUUID().toString()).generator(ChunkProvider()).createWorld()

    private class ChunkProvider() : ChunkGenerator() {
        override fun generateCaves(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            (0..15).forEach x@{ x ->
                (0..15).forEach z@{ z ->
                    (worldInfo.minHeight + 1 until worldInfo.maxHeight - 1).forEach h@{ y ->
                        val currentBlock = chunkData.getType(x, y, z)
                        if (currentBlock.isAir) return@h
                        val topBlock = chunkData.getType(x, y + 1, z)
                        if (!topBlock.isAir) return@h
                        
                    }
                }
            }
        }

        override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            super.generateBedrock(worldInfo, random, chunkX, chunkZ, chunkData)
        }

        override fun createVanillaChunkData(world: World, x: Int, z: Int): ChunkData {
            return super.createVanillaChunkData(world, x, z)
        }

        override fun shouldGenerateNoise(): Boolean {
            return true
        }

        override fun shouldGenerateSurface(): Boolean {
            return true
        }

        @Deprecated("Deprecated in Java", ReplaceWith("true"))
        override fun shouldGenerateBedrock(): Boolean {
            return true
        }

        override fun shouldGenerateCaves(): Boolean {
            return true
        }

        override fun shouldGenerateDecorations(): Boolean {
            return true
        }

        override fun shouldGenerateMobs(): Boolean {
            return true
        }

        override fun shouldGenerateStructures(): Boolean {
            return true
        }
    }

}