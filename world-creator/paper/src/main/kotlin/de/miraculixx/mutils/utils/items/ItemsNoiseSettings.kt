package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.data.GeneratorData
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsNoiseSettings(private val generatorData: GeneratorData) : ItemProvider {
    private val settingLore = listOf(emptyComponent(), cmp("• ") + cmp("Settings", cHighlight, underlined = true))

    override fun getSlotMap(): Map<ItemStack, Int> {
        return buildMap {
            val gen = generatorData.generator
            var index = 1
            put(gen.getIcon(generatorData, 0), 20)
            put(itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = emptyComponent()
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ARROW_RIGHT_WHITE.value)
            }, 22)

            gen.settings.forEach { (settingIndex, setting) ->
                put(itemStack(setting.icon) {
                    meta {
                        name = cmp(msgString("items.algo.${setting.name}.n"), cHighlight)
                        lore(msgList("items.algo.${setting.name}.l", inline = "<grey>") + settingLore + listOf(
                            cmp("   Value: ") + cmp(settingIndex.getString(generatorData), cHighlight),
                            emptyComponent()
                        ) + settingIndex.getClickLore())
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