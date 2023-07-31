package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerDesignValue
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mvanilla.extensions.lore
import de.miraculixx.mvanilla.extensions.name
import de.miraculixx.mvanilla.extensions.round
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ItemsDesignPartEditor(
    private val design: TimerDesign,
    uuid: UUID,
    private val isRunning: Boolean
) : ItemProvider {
    private val msg1 = msgString("event.forcedTwoDigits")
    private val msg2 = msgString("event.visibleOnNull")
    private val msg3 = msgString("event.prefix")
    private val msg4 = msgString("event.suffix")
    private val dummyTimer = PaperTimer(true, null, uuid, isRunning)

    override fun getSlotMap(): Map<Int, ItemStack> {
        dummyTimer.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds)
        val part = if (isRunning) design.running else design.idle
        return mapOf(
            10 to itemStack(Material.MAP) {
                meta {
                    name = cmp(msg3, cHighlight)
                    lore(buildLore("<prefix>", null, msg3, part.prefix))
                    customModel = 1
                }
            },
            11 to itemStack(Material.GOLD_BLOCK) {
                meta {
                    name = cmp(msgString("event.days"), cHighlight)
                    lore(buildLore("<d>", part.days))
                    customModel = 2
                }
            },
            12 to itemStack(Material.RAW_GOLD) {
                meta {
                    name = cmp(msgString("event.hours"), cHighlight)
                    lore(buildLore("<h>", part.hours))
                    customModel = 3
                }
            },
            13 to itemStack(Material.GOLD_INGOT) {
                meta {
                    name = cmp(msgString("event.minutes"), cHighlight)
                    lore(buildLore("<m>", part.minutes))
                    customModel = 4
                }
            },
            14 to itemStack(Material.SUNFLOWER) {
                meta {
                    name = cmp(msgString("event.seconds"), cHighlight)
                    lore(buildLore("<s>", part.seconds))
                    customModel = 5
                }
            },
            15 to itemStack(Material.GOLD_NUGGET) {
                meta {
                    name = cmp(msgString("event.millis"), cHighlight)
                    lore(buildLore("<ms>", part.millis))
                    customModel = 6
                }
            },
            16 to itemStack(Material.MAP) {
                meta {
                    name = cmp(msg4, cHighlight)
                    lore(buildLore("<suffix>", null, msg4, part.suffix))
                    customModel = 7
                }
            },
            21 to itemStack(Material.REDSTONE) {
                meta {
                    val n = msgString("event.animation")
                    name = cmp(n, cHighlight)
                    lore(
                        buildLore(null, null, n, part.animationSpeed.round(2).toString())
                            .plus(listOf(msgClickLeft + cmp("+0.01/t"), msgClickRight + cmp("-0.01/t")))
                    )
                    customModel = 8
                }
            },
            23 to itemStack(Material.BOOK) {
                meta {
                    val n = msgString("event.syntax")
                    name = cmp(n, cHighlight)
                    lore(buildLore(null, null, n, part.syntax))
                    customModel = 9
                }
            },
            31 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("event.finish"), cSuccess)
                    customModel = 10
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
            },
        )
    }

    private fun buildLore(key: String?, timerValue: TimerDesignValue?, name: String? = null, value: String? = null, blanc: Boolean = false): List<Component> {
        return buildList {
            if (key != null) add(cmp("Syntax Key: $key"))
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            if (timerValue != null) {
                add(cmp("   $msg1: ") + cmp(timerValue.forcedTwoDigits.toString(), cHighlight))
                add(cmp("   $msg2: ") + cmp(timerValue.visibleOnNull.toString(), cHighlight))
                add(cmp("   $msg3: ") + cmp(timerValue.prefix, cHighlight))
                add(cmp("   $msg4: ") + cmp(timerValue.suffix, cHighlight))
            } else add(cmp("   $name: ") + cmp(value ?: "", cHighlight))
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Design", cHighlight, underlined = true))
            add(cmp("   ∙ ", NamedTextColor.DARK_GRAY) + dummyTimer.buildFormatted(isRunning))
            add(emptyComponent())
            if (timerValue != null) {

                val msgButton = cmp(msgString("common.button") + " ", cHighlight)
                add(msgButton + cmpTranslatableVanilla("key.hotbar.1", cHighlight) + cmp(" ≫ ") + cmp("Switch $msg1"))
                add(msgButton + cmpTranslatableVanilla("key.hotbar.2", cHighlight) + cmp(" ≫ ") + cmp("Switch $msg2"))
                add(msgButton + cmpTranslatableVanilla("key.hotbar.3", cHighlight) + cmp(" ≫ ") + cmp("Change $msg3"))
                add(msgButton + cmpTranslatableVanilla("key.hotbar.4", cHighlight) + cmp(" ≫ ") + cmp("Change $msg4"))
            } else if (!blanc) add(msgClick + cmp("Change $name"))
        }
    }
}