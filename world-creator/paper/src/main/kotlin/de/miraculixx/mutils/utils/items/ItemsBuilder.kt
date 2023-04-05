package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.api.data.WorldData
import de.miraculixx.api.data.enums.Dimension
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mutils.data.getIcon
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsBuilder(private val worldData: WorldData, private val isSet: Boolean) : ItemProvider {
    private val settingsLore = listOf(emptyComponent(), cmp("• ") + cmp("Settings", cHighlight, underlined = true))

    override fun getSlotMap(): Map<Int, ItemStack> {
        return buildMap {
            put(10, itemStack(Material.NAME_TAG) {
                getCustomMeta("name", 1, getCategory1())
            })
            put(11, itemStack(Material.BEETROOT_SEEDS) {
                getCustomMeta("category", 2, getCategory1())
            })
            put(12, itemStack(Material.WHEAT_SEEDS) {
                getCustomMeta("seed", 3, getCategory1())
            })

            if (!isSet) {
                val envItem = when (worldData.environment) {
                    Dimension.NORMAL -> Material.GRASS_BLOCK
                    Dimension.NETHER -> Material.NETHERRACK
                    Dimension.THE_END -> Material.END_STONE
                }
                put(14, itemStack(envItem) {
                    getCustomMeta("env", 10, getCategory1())
                })
            }

            put(15, itemStack(Material.BIRCH_SAPLING) {
                getCustomMeta("worldType", 5, getCategory1())
            })
            put(16, itemStack(Material.SPRUCE_SAPLING) {
                getCustomMeta("biomeAlgo", 6, getCategory2())
            })
            put(40, itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 8
                    name = cmp("Create World", cSuccess)
                    lore(listOf(emptyComponent(), msgClickLeft + cmp("Create World"), msgClickRight + cmp("Preview")))
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
            })

            put(28, itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 9
                    name = cmp(msgString("items.creator.noiseAlgo.n"), cHighlight)
                    val worldDefaults = worldData.chunkDefaults
                    lore(
                        msgList("items.creator.noiseAlgo.l") + settingsLore + listOf(
                            cmp("   ${msgString("items.creator.noiseAlgo.s1")}: ") + cmp(worldDefaults.vanillaNoise.msg(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s2")}: ") + cmp(worldDefaults.vanillaCaves.msg(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s3")}: ") + cmp(worldDefaults.vanillaSurface.msg(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s4")}: ") + cmp(worldDefaults.vanillaFoliage.msg(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s5")}: ") + cmp(worldDefaults.vanillaMobs.msg(), cHighlight),
                            cmp("   ${msgString("items.creator.noiseAlgo.s6")}: ") + cmp(worldDefaults.vanillaStructures.msg(), cHighlight),
                            emptyComponent(),
                            msgClick + cmp("Open Settings")
                        )
                    )
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.GLOBE.value)
            })
            put(29, itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 0
                    name = emptyComponent()
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ARROW_RIGHT_WHITE.value)
            })

            val chunkProviders = worldData.chunkProviders
            (0..4).forEach { index ->
                val data = chunkProviders.getOrNull(index)
                if (data == null) {
                    put(index + 30, itemStack(Material.BARRIER) {
                        meta {
                            name = cmp("✖", cError)
                            customModel = 7
                            persistentDataContainer.set(NamespacedKey(namespace, "wc.id"), PersistentDataType.STRING, UUID.randomUUID().toString())
                        }
                    })
                } else put(index + 30, data.algorithm.getIcon(data.settings, 100 + index))
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
        return buildList {
            add(cmp("   ${msgString("items.creator.name.s")}: ") + cmp(worldData.worldName, cHighlight))
            add(cmp("   ${msgString("items.creator.seed.s")}: ") + cmp(worldData.seed?.toString() ?: "Random", cHighlight))
            add(cmp("   ${msgString("items.creator.category.s")}: ") + cmp(worldData.category, cHighlight))
            if (!isSet) add(cmp("   ${msgString("items.creator.env.s")}: ") + cmp(msgString("event.env.${worldData.environment.name}"), cHighlight))
            add(cmp("   ${msgString("items.creator.worldType.s")}: ") + cmp(msgString("event.gen.${worldData.worldType.name}"), cHighlight))
        }
    }

    private fun getCategory2(): List<Component> {
        return listOf(
            cmp("   ${msgString("items.creator.biomeAlgo.s")}: ") + cmp(worldData.biomeProvider.algorithm.name, cHighlight),
        )
    }
}