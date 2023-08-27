package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mtimer.vanilla.data.TimerDesign
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
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
        val dummyTimer = PaperTimer(true, null, null, false)
        dummyTimer.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
        val converter = ItemDesignConverter(TimerManager.globalTimer as PaperTimer, dummyTimer)
        return mapOf(
            11 to itemStack(Material.BOOK) {
                meta {
                    name = cmp(msgString("items.designName.n"), cHighlight)
                    customModel = 1
                    lore(buildList {
                        addAll(msgList("items.designName.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
                        add(cmp("   ${design.name}"))
                        add(emptyComponent())
                        add(msgClick + cmp("Change"))
                    })
                }
            },
            13 to converter.getItem(design, uuid),
            15 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("items.designRunning.n"), cHighlight)
                    customModel = 2
                    lore(buildList {
                        addAll(msgList("items.designRunning.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(msgClick + cmp("Open Settings"))
                    })
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CONTINUE_GREEN.value)
            },
            16 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("items.designIdle.n"), cHighlight)
                    customModel = 3
                    lore(buildList {
                        addAll(msgList("items.designIdle.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(msgClick + cmp("Open Settings"))
                    })
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.PAUSE_RED.value)
            },
            22 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("event.finish"), cSuccess)
                    customModel = 4
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
            },
        )
    }
}