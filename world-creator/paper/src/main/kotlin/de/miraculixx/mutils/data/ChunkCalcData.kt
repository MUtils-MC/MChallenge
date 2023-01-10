package de.miraculixx.mutils.data

import org.bukkit.generator.ChunkGenerator.ChunkData
import org.bukkit.generator.WorldInfo

data class ChunkCalcData(val worldInfo: WorldInfo, val chunkX: Int, val chunkZ: Int, val chunkData: ChunkData)