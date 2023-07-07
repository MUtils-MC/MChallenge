package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerDesignValue
import de.miraculixx.mtimer.module.FabricTimer
import de.miraculixx.mtimer.server
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.extensions.round
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setSkullTexture
import net.silkmc.silk.nbt.dsl.nbtCompound
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
    private val dummyTimer = FabricTimer(true, null, uuid, isRunning, server.playerList)

    override fun getSlotMap(): Map<Int, ItemStack> {
        dummyTimer.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds)
        val part = if (isRunning) design.running else design.idle
        return mapOf(
            10 to itemStack(Items.MAP) {
                setName(cmp(msg3, cHighlight))
                setLore(buildLore("<prefix>", null, msg3, part.prefix))
                addTagElement(namespace, nbtCompound { put("ID", 1) })
            },
            11 to itemStack(Items.GOLD_BLOCK) {
                setName(cmp(msgString("event.days"), cHighlight))
                setLore(buildLore("<d>", part.days))
                addTagElement(namespace, nbtCompound { put("ID", 2) })
            },
            12 to itemStack(Items.RAW_GOLD) {
                setName(cmp(msgString("event.hours"), cHighlight))
                setLore(buildLore("<h>", part.hours))
                addTagElement(namespace, nbtCompound { put("ID", 3) })
            },
            13 to itemStack(Items.GOLD_INGOT) {
                setName(cmp(msgString("event.minutes"), cHighlight))
                setLore(buildLore("<m>", part.minutes))
                addTagElement(namespace, nbtCompound { put("ID", 4) })
            },
            14 to itemStack(Items.SUNFLOWER) {
                setName(cmp(msgString("event.seconds"), cHighlight))
                setLore(buildLore("<s>", part.seconds))
                addTagElement(namespace, nbtCompound { put("ID", 5) })
            },
            15 to itemStack(Items.GOLD_NUGGET) {
                setName(cmp(msgString("event.millis"), cHighlight))
                setLore(buildLore("<ms>", part.millis))
                addTagElement(namespace, nbtCompound { put("ID", 6) })
            },
            16 to itemStack(Items.MAP) {
                setName(cmp(msg4, cHighlight))
                setLore(buildLore("<suffix>", null, msg4, part.suffix))
                addTagElement(namespace, nbtCompound { put("ID", 7) })
            },
            21 to itemStack(Items.REDSTONE) {
                val n = msgString("event.animation")
                setName(cmp(n, cHighlight))
                setLore(
                    buildLore(null, null, n, part.animationSpeed.round(2).toString())
                        .plus(listOf(msgClickLeft + cmp("+0.01/t"), msgClickRight + cmp("-0.01/t")))
                )
                addTagElement(namespace, nbtCompound { put("ID", 8) })
            },
            23 to itemStack(Items.BOOK) {
                val n = msgString("event.syntax")
                setName(cmp(n, cHighlight))
                setLore(buildLore(null, null, n, part.syntax))
                addTagElement(namespace, nbtCompound { put("ID", 9) })
            },
            31 to itemStack(Items.PLAYER_HEAD) {
                setName(cmp(msgString("event.finish"), cSuccess))
                addTagElement(namespace, nbtCompound { put("ID", 10) })
                setSkullTexture(Head64.CHECKMARK_GREEN.value)
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