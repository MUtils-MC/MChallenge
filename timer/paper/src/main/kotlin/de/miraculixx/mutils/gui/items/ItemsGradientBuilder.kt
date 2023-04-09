package de.miraculixx.mutils.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mutils.data.ColorBuilder
import de.miraculixx.mutils.data.GradientBuilder
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

class ItemsGradientBuilder(private val data: GradientBuilder) : ItemProvider {
    private val msgAnimateName = cmp(msgString("items.color.animate.n"), cHighlight)
    private val msgAnimateLore = msgList("items.color.animate.l", inline = "<grey>")
    private val msgNone = cmp(msgString("common.none"), cError)
    private val msgSettings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
    private val msgOutput = cmp("∙ ") + cmp("Output", cHighlight, underlined = true)

    override fun getSlotMap(): Map<Int, ItemStack> {
        return buildMap {
            put(10, itemStack(Material.ENDER_EYE) {
                meta {
                    name = msgAnimateName
                    lore(
                        msgAnimateLore + listOf(
                            emptyComponent(),
                            msgSettings,
                            cmp("   " + msgString("items.color.animate.s") + ": ") + cmp(this@ItemsGradientBuilder.data.isAnimated.msg())
                        )
                    )
                    customModel = 1
                }
            })
            repeat(5) { index ->
                val current = data.colors.getOrNull(index)
                if (current == null) {
                    put(12 + index, itemStack(Material.STRUCTURE_VOID) {
                        meta {
                            name = msgNone
                            lore(listOf(emptyComponent(), msgClick + cmp("Add Color")))
                            customModel = 10 + index
                        }
                    })
                } else {
                    put(12 + index, itemStack(Material.LEATHER_CHESTPLATE) {
                        meta<LeatherArmorMeta> {
                            name = cmp("Color ${index + 1}", cHighlight)
                            lore(buildLore(current))
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                            setColor(Color.fromRGB(current.getColor().value()))
                            customModel = 10 + index
                        }
                    })
                }
            }
            put(22, itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("event.finish"), cHighlight)
                    customModel = 2
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
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