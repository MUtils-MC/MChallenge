package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.nbt.dsl.nbtCompound

class ItemsOverview(private val timer: Timer, private val isPersonal: Boolean) : ItemProvider {
    private val msgActive = cmp(msgString("event.active"), cSuccess)
    private val msgDisabled = cmp(msgString("event.disabled"), cError)

    override fun getSlotMap(): Map<Int, ItemStack> {
        val time = timer.buildSimple()
        val setupLore = listOf(
            emptyComponent(),
            cmp("∙ ") + cmp("Settings", cHighlight, underlined = true),
            cmp("   ${msgString("items.time")}: ") + cmp(time, cHighlight),
            cmp("   ${msgString("items.isActive.s")}: ") + cmp(timer.visible.toString(), cHighlight),
            cmp("   ${msgString("items.direction.s")}: ") + cmp(timer.countUp.toString(), cHighlight),
            emptyComponent()
        )

        return mapOf(
            10 to itemStack(Items.SUNFLOWER) {
                addTagElement(namespace, nbtCompound { put("ID", 1) })
                setName(cmp(msgString("event.seconds"), cHighlight))
                setLore(timeLore("s", time))
            },
            11 to itemStack(Items.GOLD_INGOT) {
                addTagElement(namespace, nbtCompound { put("ID", 2) })
                setName(cmp(msgString("event.minutes"), cHighlight))
                setLore(timeLore("min", time))
            },
            19 to itemStack(Items.RAW_GOLD) {
                addTagElement(namespace, nbtCompound { put("ID", 3) })
                setName(cmp(msgString("event.hours"), cHighlight))
                setLore(timeLore("h", time))
            },
            20 to itemStack(Items.GOLD_BLOCK) {
                addTagElement(namespace, nbtCompound { put("ID", 4) })
                setName(cmp(msgString("event.days"), cHighlight))
                setLore(timeLore("d", time))
            },
            15 to itemStack(Items.CLOCK) {
                addTagElement(namespace, nbtCompound { put("ID", 7) })
                setName(cmp(msgString("items.isActive.n"), cHighlight))
                setLore(buildList<Component> {
                    addAll(msgList("items.isActive.l", inline = "<grey>"))
                    addAll(setupLore)
                    add(msgClick + cmp("Toggle"))
                })
            },
            15 + 9 to itemStack(if (timer.visible) Items.LIME_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE) {
                addTagElement(namespace, nbtCompound { put("ID", 7) })
                setName(if (timer.visible) msgActive else msgDisabled)
            },
            16 to itemStack(Items.MAGENTA_GLAZED_TERRACOTTA) {
                addTagElement(namespace, nbtCompound { put("ID", 6) })
                setName(cmp(msgString("items.direction.n"), cHighlight))
                setLore(buildList<Component> {
                    addAll(msgList("items.direction.l", inline = "<grey>"))
                    addAll(setupLore)
                    add(msgClick + cmp("Toggle"))
                })
            },
            16 + 9 to itemStack(if (timer.countUp) Items.LIME_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE) {
                addTagElement(namespace, nbtCompound { put("ID", 6) })
                setName(if (timer.countUp) cmp("Count Up", cSuccess) else cmp("Count Down", cError))
            },
            13 to itemStack(Items.NAME_TAG) {
                addTagElement(namespace, nbtCompound { put("ID", 5) })
                setName(cmp(msgString("items.design.n"), cHighlight))
                setLore(buildList<Component> {
                    addAll(msgList("items.design.l", inline = "<grey>"))
                    add(emptyComponent())
                    add(cmp("∙ ") + cmp("Current Design", cHighlight, underlined = true))
                    add(cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timer.buildFormatted(true))
                    add(emptyComponent())
                    add(msgClick + cmp("Open Menu"))
                })
            }
        ).plus(buildMap {
            if (!isPersonal) {
                put(14, itemStack(Items.SHEARS) {

                    addTagElement(namespace, nbtCompound { put("ID", 8) })
                    setName(cmp(msgString("items.rules.n"), cHighlight))
                    setLore(buildList {
                        addAll(msgList("items.rules.l", inline = "<grey>"))
                        addAll(setupLore)
                        add(msgClick + cmp("Open Menu"))
                    })

                })
                put(23, itemStack(Items.DRAGON_EGG) {

                    addTagElement(namespace, nbtCompound { put("ID", 9) })
                    setName(cmp(msgString("items.goals.n"), cHighlight))
                    setLore(buildList<Component> {
                        addAll(msgList("items.goals.l", inline = "<grey>"))
                        addAll(setupLore)
                        add(msgClick + cmp("Open Menu"))
                    })

                })
            }
        })
    }

    private fun timeLore(change: String, time: String): List<Component> {
        return listOf(
            cmp(">> ") + cmp(time, cMark),
            emptyComponent(),
            cmp("1$change", cHighlight) + cmp(" • Left-Click"),
            cmp("10$change", cHighlight) + cmp(" • Shift-Left-Click"),
            cmp("                            ", NamedTextColor.DARK_GRAY, strikethrough = true),
            cmp("-1$change", cHighlight) + cmp(" • Right-Click"),
            cmp("-10$change", cHighlight) + cmp(" • Shift-Right-Click")
        )
    }
}