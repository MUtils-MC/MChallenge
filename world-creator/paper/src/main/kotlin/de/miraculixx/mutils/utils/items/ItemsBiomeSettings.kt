package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.api.data.BiomeProviderData
import de.miraculixx.mutils.data.getIcon
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class ItemsBiomeSettings(private val biomeProviderData: BiomeProviderData): ItemProvider {
    private val settingLore = listOf(emptyComponent(), cmp("• ") + cmp("Settings", cHighlight, underlined = true))

    override fun getSlotMap(): Map<ItemStack, Int> {
        return buildMap {
            val gen = biomeProviderData.algorithm
            put(gen.getIcon(biomeProviderData.settings, 0), 20)
            put(itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = emptyComponent()
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ARROW_RIGHT_WHITE.value)
            }, 22)

            var index = 1
            gen.settings.forEach { (settingIndex, setting) ->
                put(itemStack(setting.getIcon()) {
                    meta {
                        customModel = 1
                        persistentDataContainer.set(NamespacedKey(namespace, "gui.biome.setting"), PersistentDataType.STRING, settingIndex.name)
                        name = cmp(msgString("items.algo.${setting.name}.n"), cHighlight)
                        lore(
                            msgList("items.algo.${setting.name}.l", inline = "<grey>") + settingLore + listOf(
                                cmp("   Value: ") + cmp(settingIndex.getString(biomeProviderData.settings), cHighlight),
                                emptyComponent()
                            ) + settingIndex.getClickLore()
                        )
                    }
                }, getSlot(index))
                index++
            }
            while (index < 7) {
                put(itemStack(Material.BARRIER) {
                    meta {
                        customModel = index + 10
                        name = emptyComponent()
                    }
                }, getSlot(index))
                index++
            }
        }
    }

    private fun getSlot(index: Int): Int {
        return when (index) {
            1 -> 15
            2 -> 24
            3 -> 33
            4 -> 16
            5 -> 25
            else -> 34
        }
    }
}