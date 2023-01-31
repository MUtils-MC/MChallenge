package de.miraculixx.mutils.await

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.extensions.soundError
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class AwaitConfirm(source: Player, onConfirm: () -> Unit, onCancel: () -> Unit) {
    private val gui = InventoryManager.inventoryBuilder("${source.uniqueId}-CONFIRM") {
        title = cmp("• ") + cmp(msgString("common.confirm"), NamedTextColor.DARK_GREEN)
        size = 3
        player = source
        itemProvider = InternalItemProvider()
        clickAction = InternalClickProvider(source, onConfirm, onCancel, this@AwaitConfirm).run
    }

    private val onClose = listen<InventoryCloseEvent> {
        if (it.inventory != gui.get()) return@listen
        if (it.reason != InventoryCloseEvent.Reason.PLAYER) return@listen
        disable()
        taskRunLater(1) {
            onCancel.invoke()
            it.player.soundError()
        }
    }

    private fun disable() {
        onClose.unregister()
    }

    private class InternalItemProvider: ItemProvider {
        override fun getSlotMap(): Map<ItemStack, Int> {
            return mapOf(
                itemStack(Material.PLAYER_HEAD) {
                    meta {
                        customModel = 1
                        name = cmp(msgString("common.confirm"), cSuccess)
                    }
                    itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
                } to 12,
                itemStack(Material.PLAYER_HEAD) {
                    meta {
                        customModel = 2
                        name = cmp(msgString("common.cancel"), cError)
                    }
                    itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.X_RED.value)
                } to 14
            )
        }
    }

    private class InternalClickProvider(player: Player, onConfirm: () -> Unit, onCancel: () -> Unit, confirmer: AwaitConfirm): GUIEvent {
        override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, _: CustomInventory ->
            it.isCancelled = true
            if (it.whoClicked != player) return@event

            when (it.currentItem?.itemMeta?.customModel) {
                1 -> {
                    player.closeInventory()
                    onConfirm.invoke()
                }
                2 -> {
                    player.closeInventory()
                    onCancel.invoke()
                }
                else -> return@event
            }
            confirmer.disable()
        }
    }
}