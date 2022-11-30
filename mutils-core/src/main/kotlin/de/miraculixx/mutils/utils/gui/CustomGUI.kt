package de.miraculixx.mutils.utils.gui

import de.miraculixx.mutils.utils.gui.data.CustomInventory
import de.miraculixx.mutils.utils.gui.data.InventoryManager
import de.miraculixx.mutils.utils.gui.event.GUIClickEvent
import de.miraculixx.mutils.utils.gui.event.GUICloseEvent
import de.miraculixx.mutils.utils.gui.item.itemStack
import de.miraculixx.mutils.utils.gui.item.setName
import de.miraculixx.mutils.utils.messages.*
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class CustomGUI(
    private val content: Map<ItemStack, Int>,
    title: Component,
    override val id: String,
    players: List<Player>,
    size: Int,
    clickEvent: ((GUIClickEvent) -> Unit)?,
    closeEvent: ((GUICloseEvent) -> Unit)?
) : CustomInventory(size * 9, title, clickEvent, closeEvent) {
    override val defaultClickAction: ((GUIClickEvent) -> Unit)? = null

    private constructor(builder: Builder) : this(
        builder.content,
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
        var content: Map<ItemStack, Int> = emptyMap()

        /**
         * Sets the inventory size. It defines the row count, [size] 2 will create a GUI with 18 slots (2 rows)
         */
        var size: Int = 1

        /**
         * Internal use. No need to call it inlined
         */
        fun build(): CustomGUI = CustomGUI(this)
    }

    private fun build() {
        content.forEach { (item, slot) ->
            setItem(slot, item)
        }
    }

    private fun fillPlaceholder() {
        val primaryPlaceholder = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setName(cmp(" ")) }
        val secondaryPlaceholder = itemStack(Items.BLACK_STAINED_GLASS_PANE) { setName(cmp(" ")) }

        val size = containerSize
        repeat(size) {
            setItem(it, primaryPlaceholder)
        }
        if (size != 9) {
            setItem(17, secondaryPlaceholder)
            setItem(size - 18, secondaryPlaceholder)
            repeat(2) { setItem(it, secondaryPlaceholder) }
            repeat(3) { setItem(it + 7, secondaryPlaceholder) }
            repeat(3) { setItem(size - it - 8, secondaryPlaceholder) }
            repeat(2) { setItem(size - it - 1, secondaryPlaceholder) }
        } else {
            setItem(0, secondaryPlaceholder)
            setItem(8, secondaryPlaceholder)
        }
    }


    init {
        if (players.isEmpty()) {
            consoleAudience?.sendMessage(prefix + cmp("Creating GUI without player - Unexpected behaviour", cError))
            InventoryManager.remove(id)
        } else {
            fillPlaceholder()
            build()
            open(players)
        }
    }
}

