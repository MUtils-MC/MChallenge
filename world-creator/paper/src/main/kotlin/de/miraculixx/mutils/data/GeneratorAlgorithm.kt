package de.miraculixx.mutils.data

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.api.data.GeneratorData
import de.miraculixx.api.data.enums.AlgorithmSetting
import de.miraculixx.api.data.enums.AlgorithmSettingIndex
import de.miraculixx.api.data.enums.BiomeAlgorithm
import de.miraculixx.api.data.enums.GeneratorAlgorithm
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.math.sin
import kotlin.math.sqrt


fun GeneratorAlgorithm.getGenerator(generatorData: GeneratorData): (ChunkCalcData) -> Unit {
    return when (this) {
        GeneratorAlgorithm.LINE -> { chunk: ChunkCalcData ->
            val startX = chunk.chunkX * 16
            val startZ = chunk.chunkZ * 16

            val isXDirection = generatorData.mode ?: true
            val isInverted = generatorData.invert ?: false
            val lineThickness = generatorData.x1 ?: 1
            val holeThickness = generatorData.x2 ?: 1

            val from = if (isInverted) holeThickness else lineThickness
            val to = if (isInverted) lineThickness else holeThickness + lineThickness
            (0..16).forEach d@{ d ->
                if (isXDirection) {
                    val x = startX + d
                    (from until to).forEach i@{ i ->
                        if (x % i == 0) {
                            chunk.chunkData.setRegion(d, -64, 0, d + 1, 319, 16, Material.AIR)
                            return@i
                        }
                    }
                } else {
                    val z = startZ + d
                    (from until to).forEach i@{ i ->
                        if (z % i == 0) {
                            chunk.chunkData.setRegion(0, -64, d, 16, 319, d + 1, Material.AIR)
                            return@i
                        }
                    }
                }
            }
        }

        GeneratorAlgorithm.CHESS -> { chunk: ChunkCalcData ->
            val startX = chunk.chunkX * 16
            val startZ = chunk.chunkZ * 16
            val scale = generatorData.x1 ?: 5
            val factor = scale - 1

            for (x in 0..16) {
                for (z in 0..16) {
                    val realX = startX + x
                    val realZ = startZ + z
                    val xOdd = (if (realX < 0) realX - factor else realX) / scale % 2 == 0
                    val zOdd = (if (realZ < 0) realZ - factor else realZ) / scale % 2 == 0
                    if (xOdd && zOdd || !xOdd && !zOdd) chunk.chunkData.setRegion(x, -64, z, x + 1, 319, z + 1, Material.AIR)
                }
            }
        }

        GeneratorAlgorithm.DIAGONAL_LINE -> TODO()

        GeneratorAlgorithm.SQUARE -> { chunk: ChunkCalcData ->
            val isRandom = generatorData.rnd ?: false
            val xScale = if (isRandom) (0..16).random() else generatorData.x1 ?: 1
            val zScale = if (isRandom) (0..16).random() else generatorData.x2 ?: 1
            chunk.chunkData.setRegion(xScale, -64, zScale, 16 - xScale, 319, 16 - zScale, Material.AIR)
        }

        GeneratorAlgorithm.CIRCLE -> TODO()

        GeneratorAlgorithm.SINUS -> { chunk: ChunkCalcData ->
            val zScratch = generatorData.x1 ?: 10
            val yScratch = generatorData.x2 ?: 10
            val minHeight = generatorData.x3 ?: 0

            val startX = chunk.chunkX * 16
            val startZ = chunk.chunkZ * 16

            for (x in 0..16) {
                for (z in 0..16) {
                    val realX = startX + x
                    val realZ = startZ + z
                    val dis = sqrt(.0 + realX * realX + realZ * realZ)
                    val halt = sin(dis / zScratch) * yScratch
                    chunk.chunkData.setRegion(x, minHeight + halt.toInt(), z, x + 1, chunk.chunkData.maxHeight, z + 1, Material.AIR)
                }
            }
        }
    }
}

fun GeneratorAlgorithm.getIcon(generatorData: GeneratorData, id: Int): ItemStack {
    return itemStack(Material.PLAYER_HEAD) {
        getIconMeta(this@getIcon.name, id, settings, generatorData)
        itemMeta = (itemMeta as SkullMeta).skullTexture(this@getIcon.icon)
    }
}

fun BiomeAlgorithm.getIcon(generatorData: GeneratorData, id: Int): ItemStack {
    return itemStack(Material.SPRUCE_SAPLING) {
        getIconMeta(this@getIcon.name, id, settings, generatorData)
    }
}


private fun ItemStack.getIconMeta(key: String, id: Int, settings: Map<AlgorithmSettingIndex, AlgorithmSetting>, data: GeneratorData) {
    meta {
        name = cmp(msgString("items.algo.$key.n"), cHighlight)
        customModel = id
        lore(msgList("items.algo.$key.l", inline = "<grey>") + buildList {
            if (settings.isNotEmpty()) {
                add(emptyComponent())
                add(cmp("• ") + cmp("Settings", cHighlight, underlined = true))
                settings.forEach { (settingIndex, setting) ->
                    add(cmp("   ${msgString("items.algo.${setting.name}.n")}: ") + cmp(settingIndex.getString(data), cHighlight))
                }
            }
            if (id != 0) {
                add(emptyComponent())
                add(msgClickLeft + cmp("Open Settings"))
                add(msgShiftClickRight + cmp("Delete Rule"))
            }
        })
    }
}