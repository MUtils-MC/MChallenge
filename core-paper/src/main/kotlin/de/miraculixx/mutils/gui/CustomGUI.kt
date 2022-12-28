package de.miraculixx.mutils.gui

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class CustomGUI(
    override val itemProvider: ItemProvider?,
    title: Component,
    override val id: String,
    players: List<Player>,
    size: Int,
    clickEvent: ((InventoryClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((InventoryCloseEvent, CustomInventory) -> Unit)?
) : CustomInventory(size * 9, title, clickEvent, closeEvent) {
    override val defaultClickAction: ((InventoryClickEvent) -> Unit)? = null
    private val i = get()

    private constructor(builder: Builder) : this(
        builder.itemProvider,
        builder.title,
        builder.id,
        buildList {
            addAll(builder.players)
            builder.player?.let { add(it) }
        },
        builder.size,
        builder.clickAction,
        builder.closeAction
    )

    class Builder(val id: String): CustomInventory.Builder() {
        /**
         * Import items to the custom GUI.
         */
        var itemProvider: ItemProvider? = null

        /**
         * Sets the inventory size. It defines the row count, [size] 2 will create a GUI with 18 slots (2 rows)
         */
        var size: Int = 1

        /**
         * Internal use. No need to call it inlined
         */
        fun build(): CustomGUI = CustomGUI(this)
    }

    override fun update() {
        val content = itemProvider?.getSlotMap()
        content?.forEach { (item, slot) ->
            i.setItem(slot, item)
        }
    }

    private fun fillPlaceholder() {
        val primaryPlaceholder = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
        val secondaryPlaceholder = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }

        val size = i.size
        repeat(size) {
            i.setItem(it, primaryPlaceholder)
        }
        if (size != 9) {
            i.setItem(17, secondaryPlaceholder)
            i.setItem(size - 18, secondaryPlaceholder)
            repeat(2) { i.setItem(it, secondaryPlaceholder) }
            repeat(3) { i.setItem(it + 7, secondaryPlaceholder) }
            repeat(3) { i.setItem(size - it - 8, secondaryPlaceholder) }
            repeat(2) { i.setItem(size - it - 1, secondaryPlaceholder) }
        } else {
            i.setItem(0, secondaryPlaceholder)
            i.setItem(8, secondaryPlaceholder)
        }
    }


    init {
        if (players.isEmpty()) {
            consoleAudience.sendMessage(prefix + cmp("Creating GUI without player - Unexpected behaviour", cError))
            InventoryManager.remove(id)
        } else {
            fillPlaceholder()
            update()
            open(players)
        }
    }
}

