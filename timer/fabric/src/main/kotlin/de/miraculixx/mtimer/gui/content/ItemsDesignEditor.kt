package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.module.FabricTimer
import de.miraculixx.mtimer.server
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
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

class ItemsDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID
) : ItemProvider {
    override fun getSlotMap(): Map<Int, ItemStack> {
        val dummyTimer = FabricTimer(true, null, null, false, server.playerList)
        dummyTimer.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
        val converter = ItemDesignConverter(TimerManager.globalTimer, dummyTimer, emptyList())
        return mapOf(
            11 to itemStack(Items.BOOK) {
                setName(cmp(msgString("items.designName.n"), cHighlight))
                addTagElement(namespace, nbtCompound { put("ID", 1) })
                setLore(buildList {
                    addAll(msgList("items.designName.l", inline = "<grey>"))
                    add(emptyComponent())
                    add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
                    add(cmp("   ${design.name}"))
                    add(emptyComponent())
                    add(msgClick + cmp("Change"))
                })
            },
            13 to converter.getItem(design, uuid),
            15 to itemStack(Items.PLAYER_HEAD) {
                setName(cmp(msgString("items.designRunning.n"), cHighlight))
                addTagElement(namespace, nbtCompound { put("ID", 2) })
                setLore(buildList {
                    addAll(msgList("items.designRunning.l", inline = "<grey>"))
                    add(emptyComponent())
                    add(msgClick + cmp("Open Settings"))
                })
                setSkullTexture(Head64.CONTINUE_GREEN.value)
            },
            16 to itemStack(Items.PLAYER_HEAD) {
                setName(cmp(msgString("items.designIdle.n"), cHighlight))
                addTagElement(namespace, nbtCompound { put("ID", 3) })
                setLore(buildList {
                    addAll(msgList("items.designIdle.l", inline = "<grey>"))
                    add(emptyComponent())
                    add(msgClick + cmp("Open Settings"))
                })
                setSkullTexture(Head64.PAUSE_RED.value)
            },
            22 to itemStack(Items.PLAYER_HEAD) {
                setName(cmp(msgString("event.finish"), cSuccess))
                addTagElement(namespace, nbtCompound { put("ID", 4) })
                setSkullTexture(Head64.CHECKMARK_GREEN.value)
            },
        )
    }
}