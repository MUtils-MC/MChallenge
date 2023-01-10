package de.miraculixx.mutils.data

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import org.bukkit.Material
import de.miraculixx.mutils.data.AlgorithmSettingIndex.*
import de.miraculixx.mutils.data.AlgorithmSetting.*
import de.miraculixx.mutils.messages.*
import org.bukkit.inventory.ItemStack

enum class GeneratorAlgorithm(val settings: Map<AlgorithmSettingIndex, AlgorithmSetting>,val icon: String) {
    LINE(           mapOf(X1 to SOLID_THICKNESS, X2 to HOLE_THICKNESS, MODE to X_DIRECTION, INVERT to INVERTED), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFjOGI3ZGVhMDE0NjdjYjdmZDlhM2RlYmQ2YTEyNzE0YTlhNDJhOGZkZmJkZGE1MzdmZmI3NTEwMzE4MzIyNSJ9fX0"),
    DIAGONAL_LINE(  mapOf(), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzcyOWEyOGYxM2IzYzcyMzQ2YWFjNWM2NjEwZDQyNTkxZTkwMTAzZmQxMmJjNzhlOGMzMGExZTFkMDFkNDY2MyJ9fX0"),
    CHESS(          mapOf(X1 to SCALE_X), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjEyYzlhMmRkMjk1YTRjYzhiNDBlMTE2M2RmOTY3YzNkYWZmZjA5YzkxMzRmNDhlODFhOGRjMzEzMGEyYWUxZSJ9fX0"),
    SQUARE(         mapOf(X1 to SCALE_X, X2 to SCALE_Z, RND to RANDOM), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY1Mjc5NzI5MzU5OTMwNWQ1Zjk0YTYxYTRhNzc4YzNmMmZhMmQ1ODVmMmRlMzhmYjA2YTQxMjUxYjRjODJmNCJ9fX0"),
    CIRCLE(         mapOf(), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI1NTc4NWEwMWJiMWNjNjQ0NGQzMWQ1ZDhhNTgwZDUxNWFmYTg5YWJhMGQxZjQwYTc0NDUxMzAyYmQzNWM0MCJ9fX0");

    fun getGenerator(generatorData: GeneratorData): (ChunkCalcData) -> Unit {
        return when (this) {
            LINE -> { chunk: ChunkCalcData ->
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

            CHESS -> { chunk: ChunkCalcData ->
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

            DIAGONAL_LINE -> TODO()

            SQUARE -> { chunk: ChunkCalcData ->
                val isRandom = generatorData.rnd ?: false
                val xScale = if (isRandom) (0..16).random() else generatorData.x1 ?: 1
                val zScale = if (isRandom) (0..16).random() else generatorData.x2 ?: 1
                chunk.chunkData.setRegion(xScale, -64, zScale, 16 - xScale, 319, 16 - zScale, Material.AIR)
            }

            CIRCLE -> TODO()
        }
    }

    fun getIcon(generatorData: GeneratorData, id: Int): ItemStack {
        return itemStack(Material.PLAYER_HEAD) {
            meta {
                val key = this.name
                name = cmp(msgString("items.creator.$key.n"), cHighlight)
                lore(msgList("items.creator.$key.l") + buildList {
                    settings.forEach { (settingIndex, setting) ->
                        add(cmp("   ${msgString("items.creator.${setting.name}.n")}: ") + cmp(settingIndex.getString(generatorData)))
                    }
                    if (id != 0) {
                        add(emptyComponent())
                        add(msgClickLeft + cmp("Open Settings"))
                        add(msgShiftClickRight + cmp("Delete Rule"))
                    }
                })
            }
        }
    }
}