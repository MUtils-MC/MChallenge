package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setSkullTexture
import net.silkmc.silk.nbt.dsl.nbtCompound

class ItemsColorBuilder(private val data: ColorBuilder) : ItemProvider {
    private val msgTypeName = cmp(msgString("items.color.type.n"), cHighlight, bold = true)
    private val msgTypeLore = msgList("items.color.type.l", inline = "<grey>")
    private val msgSettings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
    private val msgOutput = cmp("∙ ") + cmp("Output", cHighlight, underlined = true)

    override fun getSlotMap(): Map<Int, ItemStack> {
        val currentColor = data.getColor()
        return buildMap {
            put(11, itemStack(Items.MAGMA_CREAM) {
                setName(msgTypeName)
                setLore(msgTypeLore + buildLore(currentColor) + (msgClick + cmp("Switch")))
                addTagElement(namespace, nbtCompound { put("ID", 1) })
            })
            when (data.type) {
                ColorType.VANILLA -> {
                    val ph = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setName(emptyComponent()) }
                    put(13, ph)
                    put(15, ph)
                    put(14, itemStack(Items.LEATHER_CHESTPLATE) {
                        setName(cmp(msgString("items.color.vanilla.n"), cHighlight))
                        setLore(buildLore(currentColor) + (msgClick + cmp("Switch")))
                        hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL)
                        addTagElement(namespace, nbtCompound { put("ID", 2) })
                        getOrCreateTagElement("display").putInt("color", currentColor.value())
                    })
                }

                ColorType.RGB -> {
                    val clickLore = listOf(msgClickLeft + cmp("+1"), msgClickRight + cmp("-1"), msgShiftClickLeft + cmp("+10"), msgShiftClickRight + cmp("-10"))
                    put(13, itemStack(Items.RED_DYE) {
                        setName(cmp(msgString("items.color.red.n"), cHighlight))
                        setLore(buildLore(currentColor) + clickLore)
                        addTagElement(namespace, nbtCompound { put("ID", 3) })
                    })
                    put(14, itemStack(Items.GREEN_DYE) {
                        setName(cmp(msgString("items.color.green.n"), cHighlight))
                        setLore(buildLore(currentColor) + clickLore)
                        addTagElement(namespace, nbtCompound { put("ID", 4) })
                    })
                    put(15, itemStack(Items.BLUE_DYE) {
                        setName(cmp(msgString("items.color.blue.n"), cHighlight))
                        setLore(buildLore(currentColor) + clickLore)
                        addTagElement(namespace, nbtCompound { put("ID", 5) })
                    })
                }

                ColorType.HEX_CODE -> {
                    val ph = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setName(emptyComponent()) }
                    put(13, ph)
                    put(15, ph)
                    put(14, itemStack(Items.LEATHER_CHESTPLATE) {
                        setName(cmp(msgString("items.color.hex.n"), cHighlight))
                        setLore(buildLore(currentColor) + (msgClick + cmp("Enter")))
                        addTagElement(namespace, nbtCompound { put("ID", 6) })
                        hideTooltipPart(ItemStack.TooltipPart.DYE)
                        hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL)

                        getOrCreateTagElement("display").putInt("color", currentColor.value())
                    })
                }
            }
            put(22, itemStack(Items.PLAYER_HEAD) {
                setName(cmp(msgString("event.finish"), cHighlight))
                addTagElement(namespace, nbtCompound { put("ID", 10) })
                setSkullTexture(Head64.CHECKMARK_GREEN.value)
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