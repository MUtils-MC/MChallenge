package de.miraculixx.mutils.gui

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class SettingsGUI(
    override val itemProvider: ItemProvider?,
    override val id: String,
    title: Component,
    players: List<Player>,
    clickEvent: ((InventoryClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((InventoryCloseEvent, CustomInventory) -> Unit)?,
) : CustomInventory(3 * 9, title, clickEvent, closeEvent) {
    private val i = get()
    override val defaultClickAction = null

    private constructor(builder: Builder) : this(
        builder.itemProvider,
        builder.id,
        builder.title,
        buildList {
            addAll(builder.players)
            builder.player?.let { add(it) }
        },
        builder.clickAction,
        builder.closeAction
    )

    class Builder(val id: String) : CustomInventory.Builder() {
        /**
         * Internal function
         */
        fun build() = SettingsGUI(this)
    }

    override fun update() {
        val content = itemProvider?.getItemList() ?: return
        fillPlaceholder(false)

        when (content.size) {
            1 -> i.setItem(13, content[0])
            2 -> {
                i.setItem(12, content[0])
                i.setItem(14, content[1])
            }

            3 -> {
                i.setItem(11, content[0])
                i.setItem(13, content[1])
                i.setItem(15, content[2])
            }

            4 -> {
                i.setItem(10, content[0])
                i.setItem(12, content[1])
                i.setItem(14, content[2])
                i.setItem(16, content[3])
            }
        }
    }

    private fun fillPlaceholder(full: Boolean) {
        val primaryPlaceholder = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
        val secondaryPlaceholder = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }

        if (full) {
            i.contents = (0..26).map { primaryPlaceholder }.toTypedArray()
            listOf(0, 1, 7, 8, 9, 17, 18, 19, 25).forEach { i.setItem(it, secondaryPlaceholder) }
            itemProvider?.getExtra()?.firstOrNull()?.let { i.setItem(26, it) }
        } else listOf(10, 11, 12, 13, 14, 15, 16).forEach { i.setItem(it, primaryPlaceholder) }
    }

    init {
        if (players.isEmpty()) {
            consoleAudience.sendMessage(prefix + cmp("Creating GUI without player - Unexpected behaviour", cError))
            InventoryManager.remove(id)
        } else {
            fillPlaceholder(true)
            update()
            open(players)
        }
    }
}