package de.miraculixx.mutils.gui.data

import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.event.GUICloseEvent
import de.miraculixx.mutils.gui.event.GUIEventHandler
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType

abstract class CustomInventory(
    size: Int,
    private val title: Component,
    private val clickEvent: ((GUIClickEvent) -> Unit)?,
    private val closeEvent: ((GUICloseEvent) -> Unit)?
    ) : SimpleContainer(size), MenuProvider {
    val viewers: MutableMap<Player, ChestMenu> = mutableMapOf()
    abstract val id: String
    abstract val defaultClickAction: ((GUIClickEvent) -> Unit)?

    /**
     * Get the final inventory object for further operations.
     * @return Crafted storage GUI
     */
    fun get(): Container {
        return this
    }

    /**
     * Close the GUI for all viewers
     */
    fun close() {
        viewers.forEach { (player, _) ->
            stopOpen(player)
        }
        viewers.clear()
    }

    /**
     * Close the GUI for a specific player
     * @param player The targeting player
     * @return False if the player is not a viewer
     */
    fun close(player: Player): Boolean {
        return if (viewers.contains(player)) {
            stopOpen(player)
            true
        } else false
    }

    /**
     * Open this GUI for a player. All players mentioned in the builder phase are automatically forced to open the GUI
     * @param player Target Player
     */
    fun open(player: Player) {
        if (debug) consoleAudience.sendMessage(prefix + cmp("Open GUI '$id' to ${player.scoreboardName}"))
        (player as ServerPlayer).openMenu(this)
    }

    /**
     * Open this GUI for multiple players. All players mentioned in the builder phase are automatically forced to open the GUI
     * @param players Target Player collection
     */
    fun open(players: Collection<Player>) {
        players.forEach { open(it) }
    }

    /**
     * Internal function to create the GUI. [inventory] is act the player inventory and is ignored in 99% of all cases
     */
    override fun createMenu(syncId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        val height = containerSize / 9
        val menu = GUIEventHandler(getMenuType(height), syncId, inventory, this, height, clickEvent, closeEvent, defaultClickAction)
        viewers[player] = menu
        return menu
    }

    /**
     * Internal event ot detect players closing the GUI
     */
    override fun stopOpen(player: Player) {
        viewers -= player
        if (viewers.isEmpty()) {
            InventoryManager.remove(id)
            if (debug) consoleAudience.sendMessage(prefix + cmp("Removing GUI '$id' from cache"))
        }
    }

    /**
     * Internal function to get GUI title
     */
    override fun getDisplayName(): net.minecraft.network.chat.Component {
//        return title
        return net.minecraft.network.chat.Component.empty()
    }

    private fun getMenuType(height: Int): MenuType<*> {
        return when (height) {
            1 -> MenuType.GENERIC_9x1
            2 -> MenuType.GENERIC_9x2
            3 -> MenuType.GENERIC_9x3
            4 -> MenuType.GENERIC_9x4
            5 -> MenuType.GENERIC_9x5
            else -> MenuType.GENERIC_9x6
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
         * Inject a click logic directly into this GUI. This will automatically be removed with the inventory after all player close it.
         *
         * [GUIClickEvent] for more information
         */
        var clickAction: ((GUIClickEvent) -> Unit)? = null

        /**
         * Inject a GUI close logic directly into this GUI. This will automatically be removed with the inventory after all player close it (but still be called for the last player).
         *
         * [GUICloseEvent] for more information
         */
        var closeAction: ((GUICloseEvent) -> Unit)? = null
    }
}