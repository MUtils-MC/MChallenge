package de.miraculixx.mutils.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.*
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.Timer
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsOverview(private val timer: Timer, private val isPersonal: Boolean): ItemProvider {
    private val msgActive = cmp(msgString("event.active"), cSuccess)
    private val msgDisabled = cmp(msgString("event.disabled"), cError)

    override fun getSlotMap(): Map<ItemStack, Int> {
        val time = timer.buildSimple()
        val setupLore = listOf(
            emptyComponent(),
            cmp("∙ ") + cmp("Settings", cHighlight, underlined = true),
            cmp("   ${msgString("items.time")}: ") + cmp(time, cHighlight),
            cmp("   ${msgString("items.isActive")}: ") + cmp(timer.visible.toString(), cHighlight),
            cmp("   ${msgString("items.countUp")}: ") + cmp(timer.countUp.toString(), cHighlight),
            emptyComponent()
        )

        return mapOf(
            itemStack(Material.SUNFLOWER) {
                meta {
                    customModel = 1
                    name = cmp(msgString("event.seconds"), cHighlight)
                    lore(timeLore("s", time))
                }
            } to 10,
            itemStack(Material.GOLD_INGOT) {
                meta {
                    customModel = 2
                    name = cmp(msgString("event.minutes"), cHighlight)
                    lore(timeLore("min", time))
                }
            } to 11,
            itemStack(Material.RAW_GOLD) {
                meta {
                    customModel = 3
                    name = cmp(msgString("event.hours"), cHighlight)
                    lore(timeLore("h", time))
                }
            } to 19,
            itemStack(Material.GOLD_BLOCK) {
                meta {
                    customModel = 4
                    name = cmp(msgString("event.days"), cHighlight)
                    lore(timeLore("d", time))
                }
            } to 20,
            itemStack(Material.CLOCK) {
                meta {
                    customModel = 7
                    name = cmp(msgString("items.isActive.n"), cHighlight)
                    lore(buildList {
                        addAll(msgList("items.isActive.l", inline = "<grey>"))
                        addAll(setupLore)
                        add(msgClick + cmp("Toggle"))
                    })
                }
            } to 15,
            itemStack(if (timer.visible) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    customModel = 7
                    name = if (timer.visible) msgActive else msgDisabled
                }
            } to 15 + 9,
            itemStack(Material.MAGENTA_GLAZED_TERRACOTTA) {
                meta {
                    customModel = 6
                    name = cmp(msgString("items.direction.n"), cHighlight)
                    lore(buildList {
                        addAll(msgList("items.direction.l", inline = "<grey>"))
                        addAll(setupLore)
                        add(msgClick + cmp("Toggle"))
                    })
                }
            } to 16,
            itemStack(if (timer.countUp) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    customModel = 6
                    name = if (timer.countUp) cmp("Count Up", cSuccess) else cmp("Count Down", cError)
                }
            } to 16 + 9,
            itemStack(Material.NAME_TAG) {
                meta {
                    customModel = 5
                    name = cmp(msgString("items.design.n"), cHighlight)
                    lore(buildList {
                        addAll(msgList("items.design.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(cmp("∙ ") + cmp("Current Design", cHighlight, underlined = true))
                        add(cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timer.buildFormatted(true))
                        add(emptyComponent())
                        add(msgClick + cmp("Open Menu"))
                    })
                }
            } to 13
        ).plus(buildMap {
            if (!isPersonal) {
                put(itemStack(Material.SHEARS) {
                    meta {
                        customModel = 8
                        name = cmp(msgString("items.rules.n"), cHighlight)
                        lore(buildList {
                            addAll(msgList("items.rules.l", inline = "<grey>"))
                            addAll(setupLore)
                            add(msgClick + cmp("Open Menu"))
                        })
                    }
                }, 14)
                put(itemStack(Material.DRAGON_EGG) {
                    meta {
                        customModel = 9
                        name = cmp(msgString("items.goals.n"), cHighlight)
                        lore(buildList {
                            addAll(msgList("items.goals.l", inline = "<grey>"))
                            addAll(setupLore)
                            add(msgClick + cmp("Open Menu"))
                        })
                    }
                }, 23)
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