package de.miraculixx.mcore.gui.data

import de.miraculixx.mcore.gui.*

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
    inline fun storageBuilder(id: String, builder: StorageGUI.Builder.() -> Unit) = add(id, StorageGUI.Builder(id).apply(builder).build())

    /**
     * Inline Builder for GUI type - Scrolling
     *
     * Use scroll GUIs to display activation status for your settings. Should not contain more than 64 settings for best experience.
     * @author Miraculixx
     */
    inline fun scrollBuilder(id: String, builder: ScrollGUI.Builder.() -> Unit) = add(id, ScrollGUI.Builder(id).apply(builder).build())

    /**
     * Inline Builder for GUI type - Settings
     *
     * Use settings GUIs to display 1-4 items in a central and compact design. Items will align to the center and with a one slot gap between
     * @author Miraculixx
     */
    inline fun settingsBuilder(id: String, builder: SettingsGUI.Builder.() -> Unit) = add(id, SettingsGUI.Builder(id).apply(builder).build())

    /**
     * Inline Builder for GUI type - Library
     *
     * @author Miraculixx
     */
    inline fun libraryBuilder(id: String, builder: LibraryGUI.Builder.() -> Unit) = add(id, LibraryGUI.Builder(id).apply(builder).build())
}