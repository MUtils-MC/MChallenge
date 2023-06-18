package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.GradientBuilder
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setSkullTexture
import net.silkmc.silk.nbt.dsl.nbtCompound

class ItemsGradientBuilder(private val data: GradientBuilder) : ItemProvider {
    private val msgAnimateName = cmp(msgString("items.color.animate.n"), cHighlight)
    private val msgAnimateLore = msgList("items.color.animate.l", inline = "<grey>")
    private val msgNone = cmp(msgString("common.none"), cError)
    private val msgSettings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
    private val msgOutput = cmp("∙ ") + cmp("Output", cHighlight, underlined = true)

    override fun getSlotMap(): Map<Int, ItemStack> {
        return buildMap {
            put(10, itemStack(Items.ENDER_EYE) {
                setName(msgAnimateName)
                setLore(
                    msgAnimateLore + listOf(
                        emptyComponent(),
                        msgSettings,
                        cmp("   " + msgString("items.color.animate.s") + ": ") + cmp(this@ItemsGradientBuilder.data.isAnimated.msg())
                    )
                )
                addTagElement(namespace, nbtCompound { put("ID", 1) })
            })
            repeat(5) { index ->
                val current = data.colors.getOrNull(index)
                if (current == null) {
                    put(12 + index, itemStack(Items.STRUCTURE_VOID) {
                        setName(msgNone)
                        setLore(listOf(emptyComponent(), msgClick + cmp("Add Color")))
                        addTagElement(namespace, nbtCompound { put("ID", 10 + index) })
                    })
                } else {
                    put(12 + index, itemStack(Items.LEATHER_CHESTPLATE) {
                        setName(cmp("Color ${index + 1}", cHighlight))
                        setLore(buildLore(current))
                        hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL)
                        getOrCreateTagElement("display").putInt("color", current.getColor().value())
                        addTagElement(namespace, nbtCompound { put("ID", 10 + index) })
                    })
                }
            }
            put(22, itemStack(Items.PLAYER_HEAD) {
                setName(cmp(msgString("event.finish"), cHighlight))
                addTagElement(namespace, nbtCompound { put("ID", 2) })
                setSkullTexture(Head64.CHECKMARK_GREEN.value)
            })
        }
    }

    private fun buildLore(data: ColorBuilder): List<Component> {
        return buildList {
            add(msgOutput)
            add(cmp("   (╯°□°）╯︵ ┻━┻", data.getColor()))
            add(emptyComponent())
            add(msgClickLeft + cmp("Change Color"))
            add(msgShiftClickRight + cmp("Delete"))
        }
    }
}