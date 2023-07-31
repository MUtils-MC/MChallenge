package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import de.miraculixx.mvanilla.extensions.lore
import de.miraculixx.mvanilla.extensions.name
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

class ItemsColorBuilder(private val data: ColorBuilder) : ItemProvider {
    private val msgTypeName = cmp(msgString("items.color.type.n"), cHighlight, bold = true)
    private val msgTypeLore = msgList("items.color.type.l", inline = "<grey>")
    private val msgSettings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
    private val msgOutput = cmp("∙ ") + cmp("Output", cHighlight, underlined = true)

    override fun getSlotMap(): Map<Int, ItemStack> {
        val currentColor = data.getColor()
        return buildMap {
            put(11, itemStack(Material.MAGMA_CREAM) {
                meta {
                    name = msgTypeName
                    lore(msgTypeLore + buildLore(currentColor) + (msgClick + cmp("Switch")))
                    customModel = 1
                }
            })
            when (data.type) {
                ColorType.VANILLA -> {
                    put(13, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 99; emptyComponent() } }) //TODO
                    put(15, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 98; emptyComponent() } })
                    put(14, itemStack(Material.LEATHER_CHESTPLATE) {
                        meta<LeatherArmorMeta> {
                            name = cmp(msgString("items.color.vanilla.n"), cHighlight)
                            lore(buildLore(currentColor) + (msgClick + cmp("Switch")))
                            customModel = 2
                            setColor(Color.fromRGB(currentColor.value()))
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        }
                    })
                }

                ColorType.RGB -> {
                    val clickLore = listOf(msgClickLeft + cmp("+1"), msgClickRight + cmp("-1"), msgShiftClickLeft + cmp("+10"), msgShiftClickRight + cmp("-10"))
                    put(13, itemStack(Material.RED_DYE) {
                        meta {
                            name = cmp(msgString("items.color.red.n"), cHighlight)
                            lore(buildLore(currentColor) + clickLore)
                            customModel = 3
                        }
                    })
                    put(14, itemStack(Material.GREEN_DYE) {
                        meta {
                            name = cmp(msgString("items.color.green.n"), cHighlight)
                            lore(buildLore(currentColor) + clickLore)
                            customModel = 4
                        }
                    })
                    put(15, itemStack(Material.BLUE_DYE) {
                        meta {
                            name = cmp(msgString("items.color.blue.n"), cHighlight)
                            lore(buildLore(currentColor) + clickLore)
                            customModel = 5
                        }
                    })
                }

                ColorType.HEX_CODE -> {
                    put(13, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 99; emptyComponent() } })
                    put(15, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 98; emptyComponent() } })
                    put(14, itemStack(Material.LEATHER_CHESTPLATE) {
                        meta<LeatherArmorMeta> {
                            name = cmp(msgString("items.color.hex.n"), cHighlight)
                            lore(buildLore(currentColor) + (msgClick + cmp("Enter")))
                            customModel = 6
                            setColor(Color.fromRGB(currentColor.value()))
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)
                        }
                    })
                }
            }
            put(22, itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("event.finish"), cHighlight)
                    customModel = 10
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
            })
        }
    }

    private fun buildLore(color: TextColor): List<Component> {
        return buildList {
            add(emptyComponent())
            add(msgSettings)
            when (data.type) {
                ColorType.RGB -> {
                    add(cmp("   R: ") + cmp("${data.r}", cHighlight))
                    add(cmp("   G: ") + cmp("${data.g}", cHighlight))
                    add(cmp("   B: ") + cmp("${data.b}", cHighlight))
                }

                ColorType.VANILLA -> add(cmp("   Color: ") + cmp(data.input, cHighlight))
                ColorType.HEX_CODE -> add(cmp("   Hex: ") + cmp(data.input, cHighlight))
            }
            add(emptyComponent())
            add(msgOutput)
            add(cmp("   (╯°□°）╯︵ ┻━┻", color))
            add(emptyComponent())
        }
    }
}