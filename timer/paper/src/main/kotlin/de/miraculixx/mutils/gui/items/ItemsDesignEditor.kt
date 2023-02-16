package de.miraculixx.mutils.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.data.TimerDesign
import de.miraculixx.mutils.gui.Head64
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.Timer
import de.miraculixx.mutils.module.TimerManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ItemsDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID
) : ItemProvider {
    override fun getSlotMap(): Map<ItemStack, Int> {
        val dummyTimer = Timer(true, null, null, false)
        dummyTimer.setTime(1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
        val converter = ItemDesignConverter(TimerManager.getGlobalTimer(), dummyTimer)
        return mapOf(
            itemStack(Material.BOOK) {
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
            } to 11,
            converter.getItem(design, uuid) to 13,
            itemStack(Material.PLAYER_HEAD) {
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
            } to 15,
            itemStack(Material.PLAYER_HEAD) {
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
            } to 16,
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("event.finish"), cSuccess)
                    customModel = 4
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
            } to 22,
        )
    }
}