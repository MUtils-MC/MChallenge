package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.data.WorldData
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.extensions.msg
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsBuilder(private val worldData: WorldData) : ItemProvider {
    private val settingsLore = listOf(emptyComponent(), cmp("• ") + cmp("Settings", cHighlight, underlined = true))

    override fun getSlotMap(): Map<ItemStack, Int> {
        return buildMap {
            put(itemStack(Material.NAME_TAG) {
                getCustomMeta("name", 1, getCategory1())
            }, 10)
            put(itemStack(Material.WHEAT_SEEDS) {
                getCustomMeta("seed", 2, getCategory1())
            }, 11)
            val envItem = when (worldData.environment) {
                World.Environment.NORMAL -> Material.GRASS_BLOCK
                World.Environment.NETHER -> Material.NETHERRACK
                World.Environment.THE_END -> Material.END_STONE
                World.Environment.CUSTOM -> Material.BARRIER
            }
            put(itemStack(envItem) {
                getCustomMeta("env", 3, getCategory1())
            }, 12)
            put(itemStack(Material.END_PORTAL_FRAME) {
                getCustomMeta("structures", 4, getCategory1())
            }, 14)
            put(itemStack(Material.BIRCH_SAPLING) {
                getCustomMeta("worldType", 5, getCategory1())
            }, 15)
            put(itemStack(Material.SPRUCE_SAPLING) {
                getCustomMeta("biomeAlgo", 6, getCategory2())
            }, 16)
            put(itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 8
                    name = cmp("Create World", cSuccess) //TODO
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
            }, 40)

            put(itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 0
                    name = cmp(msgString("items.creator.noiseAlgo.n"), cHighlight)
                    val worldDefaults = worldData.chunkDefaults
                    lore(
                        msgList("items.creator.noiseAlgo.l") + settingsLore + listOf(
                            cmp("   ${msgString("items.creator.noiseAlgo.s1")}: ") + cmp(worldDefaults.vanillaNoise.toString(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s2")}: ") + cmp(worldDefaults.vanillaCaves.toString(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s3")}: ") + cmp(worldDefaults.vanillaSurface.toString(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s4")}: ") + cmp(worldDefaults.vanillaFoliage.toString(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s5")}: ") + cmp(worldDefaults.vanillaMobs.toString(), cHighlight),
                            emptyComponent(),
                            msgClick + cmp("Open Settings")
                        )
                    )
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.GLOBE.value)
            }, 28)
            put(itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 0
                    name = emptyComponent()
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ARROW_RIGHT_WHITE.value)
            }, 29)

            val chunkProviders = worldData.chunkProviders
            (0..4).forEach { index ->
                val data = chunkProviders.getOrNull(index)
                if (data == null) {
                    put(itemStack(Material.BARRIER) {
                        meta {
                            name = cmp("✖", cError)
                            customModel = 7
                            persistentDataContainer.set(NamespacedKey(namespace, "wc.id"), PersistentDataType.STRING, UUID.randomUUID().toString())
                        }
                    }, index + 30)
                } else put(data.generator.getIcon(data, 100 + index), index + 30)
            }
        }
    }

    private fun ItemStack.getCustomMeta(key: String, id: Int, infoLore: List<Component>) {
        meta {
            customModel = id
            name = cmp(msgString("items.creator.$key.n"), cHighlight)
            lore(
                msgList("items.creator.$key.l", inline = "<grey>") + settingsLore + infoLore + listOf(
                    emptyComponent(),
                    msgClick + cmp("Change ${msgString("items.creator.$key.s")}")
                )
            )
        }
    }

    private fun getCategory1(): List<Component> {
        return listOf(
            cmp("   ${msgString("items.creator.name.s")}: ") + cmp(worldData.worldName, cHighlight),
            cmp("   ${msgString("items.creator.seed.s")}: ") + cmp(worldData.seed?.toString() ?: "Random", cHighlight),
            cmp("   ${msgString("items.creator.env.s")}: ") + cmp(msgString("event.env.${worldData.environment.name}"), cHighlight),
            cmp("   ${msgString("items.creator.structures.s")}: ") + cmp(worldData.generateStructures.msg(), cHighlight),
            cmp("   ${msgString("items.creator.worldType.s")}: ") + cmp(msgString("event.gen.${worldData.worldType.name}"), cHighlight),
        )
    }

    private fun getCategory2(): List<Component> {
        return listOf(
            cmp("   ${msgString("items.creator.biomeAlgo.s")}: ") + cmp(worldData.biomeProvider.algorithm.name, cHighlight),
        )
    }
}