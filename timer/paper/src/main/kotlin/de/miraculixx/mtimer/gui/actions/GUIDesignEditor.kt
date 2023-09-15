package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.mcore.await.AwaitChatMessage
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mtimer.MTimer
import de.miraculixx.mtimer.vanilla.data.TimerDesign
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsDesignPartEditor
import de.miraculixx.mtimer.gui.items.ItemsDesigns
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.extensions.soundStone
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.emptyComponent
import de.miraculixx.mvanilla.messages.msg
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class GUIDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID,
    private val isPersonal: Boolean,
) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem
        when (item?.itemMeta?.customModel ?: 0) {
            1 -> de.miraculixx.mcore.await.AwaitChatMessage(false, player, "design name", 30, design.name, false, cmp("\n"), {
                design.name = if (it.length > 30) it.dropLast(it.length - 30) else it
                player.soundEnable()
            }) {
                TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uniqueId.toString(), inv.itemProvider, this)
            }

            2 -> {
                player.closeInventory()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignPartEditor(design, uuid, true), GUIDesignPartEditor(design, uuid, true, isPersonal))
            }

            3 -> {
                player.closeInventory()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignPartEditor(design, uuid, false), GUIDesignPartEditor(design, uuid, false, isPersonal))
            }

            4 -> {
                player.closeInventory()
                player.soundEnable()
                val timer = if (isPersonal) TimerManager.getPersonalTimer(player.uniqueId) ?: return@event else TimerManager.globalTimer
                async { TimerManager.save(MTimer.configFolder) }
                TimerGUI.DESIGN.buildInventory(player, player.uniqueId.toString(), ItemsDesigns(timer), GUIDesigns(isPersonal, timer))
            }

            5 -> {
                when (it.hotbarButton) {
                    0 -> {
                        val entries = BossBar.Color.entries
                        val index = entries.indexOf(design.barColor) + 1
                        design.barColor = if (index >= entries.size) entries[0] else entries[index]
                        player.click()
                        inv.update()
                    }

                    1 -> {
                        AwaitChatMessage(false, player, "End Sound", 120, design.stopSound.key, false, msg("event.soundEnd"), {
                            design.stopSound.key = it
                            player.soundEnable()
                        }) {
                            sync {
                                inv.update()
                                inv.open(player)
                            }
                        }
                    }

                    2 -> {
                        AwaitChatMessage(false, player, "End Sound Pitch", 30, design.stopSound.pitch.toString(), false, emptyComponent(), {
                            design.stopSound.pitch = (it.toFloatOrNull() ?: 0f).coerceIn(0f, 2f)
                            player.soundEnable()
                        }) {
                            sync {
                                inv.update()
                                inv.open(player)
                            }
                        }
                    }

                    else -> player.soundStone()
                }
            }
        }
    }
}