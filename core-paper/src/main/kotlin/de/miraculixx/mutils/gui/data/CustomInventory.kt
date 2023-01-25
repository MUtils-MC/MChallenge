package de.miraculixx.mutils.gui.data

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

abstract class CustomInventory(
    size: Int,
    title: Component,
    private val clickEvent: ((InventoryClickEvent, CustomInventory) -> Unit)?,
    private val closeEvent: ((InventoryCloseEvent, CustomInventory) -> Unit)?
    ) {
    val viewers: MutableList<Player> = mutableListOf()
    private val inventory = Bukkit.createInventory(null, size, title)
    abstract val id: String
    abstract val itemProvider: ItemProvider?
    abstract val defaultClickAction: ((InventoryClickEvent) -> Unit)?

    /**
     * Get the final inventory object for further operations.
     * @return Crafted storage GUI
     */
    fun get(): Inventory {
        return inventory
    }

    /**
     * Close the GUI for all viewers
     */
    fun close(): Int {
        viewers.forEach { player ->
            close(player)
        }
        viewers.clear()
        return 0
    }

    /**
     * Close the GUI for a specific player
     * @param player The targeting player
     * @return False if the player is not a viewer
     */
    fun close(player: Player): Boolean {
        return if (viewers.contains(player)) {
            viewers -= player
            if (viewers.isEmpty()) {
                onClick.unregister()
                onClose.unregister()
                InventoryManager.remove(id)
                if (debug) consoleAudience.sendMessage(prefix + cmp("Removing GUI '$id' from cache"))
            }
            true
        } else false
    }

    /**
     * Open this GUI for a player. All players mentioned in the builder phase are automatically forced to open the GUI
     * @param player Target Player
     */
    fun open(player: Player) {
        if (debug) consoleAudience.sendMessage(prefix + cmp("Open GUI '$id' to ${player.name}"))
        viewers.add(player)
        player.openInventory(inventory)
    }

    /**
     * Open this GUI for multiple players. All players mentioned in the builder phase are automatically forced to open the GUI
     * @param players Target Player collection
     */
    fun open(players: Collection<Player>) {
        players.forEach { open(it) }
    }

    /**
     * Update the content inside the GUI. Fist call on inventory creation
     */
    abstract fun update()

    private val onClick = listen<InventoryClickEvent> {
        if (it.inventory != inventory) return@listen
        defaultClickAction?.invoke(it)
        clickEvent?.invoke(it, this)
    }

    private val onClose = listen<InventoryCloseEvent> {
        if (it.inventory != inventory) return@listen
        closeEvent?.invoke(it, this)
        viewers.remove(it.player)
        if (viewers.isEmpty()) {
            if (debug) consoleAudience.sendMessage(prefix + cmp("GUI removed: $id"))
            InventoryManager.remove(id)
        }
    }

    abstract class Builder {
        /**
         * Connect players to this GUI instance. Providing no player will lead to an instant removal of this GUI from cache.
         *
         * Use [player] for only one player
         */
        var players: List<Player> = emptyList()

        /**
         * Connect a player to this GUI instance. Providing no player will lead to an instant removal of this GUI from cache.
         *
         * Use [players] for multi-view
         */
        var player: Player? = null

        /**
         * Sets the inventory title for this custom GUI.
         */
        var title: Component = emptyComponent()

        /**
         * Import an item provider that handles all content inside this GUI. Depending on the GUI type, different functions will be called to update the content
         */
        var itemProvider: ItemProvider? = null

        /**
         * Inject a click logic directly into this GUI. This will automatically be removed with the inventory after all player close it.
         *
         * [InventoryClickEvent] for more information
         */
        var clickAction: ((InventoryClickEvent, CustomInventory) -> Unit)? = null

        /**
         * Inject a GUI close logic directly into this GUI. This will automatically be removed with the inventory after all player close it (but still be called for the last player).
         *
         * [InventoryCloseEvent] for more information
         */
        var closeAction: ((InventoryCloseEvent, CustomInventory) -> Unit)? = null
    }
}