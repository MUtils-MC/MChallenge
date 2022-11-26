package de.miraculixx.mutils.utils.gui.data

import de.miraculixx.mutils.utils.gui.CustomGUI
import de.miraculixx.mutils.utils.gui.ScrollGUI
import de.miraculixx.mutils.utils.gui.StorageGUI

object InventoryManager {
    /**
     * Stores all open GUIs for multi-use purpose.
     * [String] represents the task ID. Number IDs are used for user specific GUIs that should not sync
     * [CustomInventory] represents the GUI task
     */
    private val views: MutableMap<String, CustomInventory> = mutableMapOf()

    /**
     * Close and remove all currently open GUIs for all players
     */
    fun closeAll() {
        views.forEach { (_, gui) ->
            gui.close()
        }
        views.clear()
    }

    /**
     * Get a currently open GUI via its ID for multi-views
     * @param id Unique GUI ID
     * @return GUI instance, null if no one found
     */
    fun get(id: String): CustomInventory? {
        return views[id]
    }

    /**
     * Adds a GUI to the view list.
     *
     * WARNING! Adding GUIs with no viewer will result in permanent GUI's!
     * Use Inventory Builder like [inventoryBuilder] to be safe!
     */
    fun add(id: String, gui: CustomInventory): CustomInventory {
        views[id] = gui
        return gui
    }

    fun remove(id: String): Boolean {
        views[id]?.close() ?: return false
        return views.remove(id) != null
    }

    /**
     * Inline Builder for GUI type - Custom
     *
     * Use storage GUIs to display a lot of content with a minimal of placeholders. They can be filtered, scrollable and supports menus
     * @author Miraculixx
     */
    inline fun inventoryBuilder(id: String, builder: CustomGUI.Builder.() -> Unit) = add(id, CustomGUI.Builder(id).apply(builder).build())

    /**
     * Inline Builder for GUI type - Storages
     *
     * Use storage GUIs to display a lot of content with a minimal of placeholders. They can be filtered, scrollable and supports menus
     * @author Miraculixx
     */
    inline fun storageBuilder(id: String, builder: StorageGUI.Builder.() -> Unit) = StorageGUI.Builder(id).apply(builder).build()

    /**
     * Inline Builder for GUI type - Scrolling
     *
     * Use scroll GUIs to display activation status for your settings. Should not contain more than 64 settings for best experience.
     * @author Miraculixx
     */
    internal fun scrollBuilder(id: String, builder: ScrollGUI.Builder.() -> Unit) = ScrollGUI.Builder(id).apply(builder).build()
}