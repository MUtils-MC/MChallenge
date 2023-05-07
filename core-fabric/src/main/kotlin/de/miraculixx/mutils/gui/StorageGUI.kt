package de.miraculixx.mutils.gui

import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.event.GUICloseEvent
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.messages.emptyComponent
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.silkmc.silk.core.item.itemStack

class StorageGUI(
    private val content: Map<ItemStack, Boolean>,
    private val header: List<ItemStack>,
    private val filterable: Boolean,
    filterName: String?,
    private val scrollable: Boolean,
    private val players: List<Player>,
    title: Component,
    override val id: String,
    clickEvent: ((GUIClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((GUICloseEvent, CustomInventory) -> Unit)?
) : CustomInventory(6 * 9, title, clickEvent, closeEvent) {
    private var filter: String? = filterName
    override val defaultClickAction: ((GUIClickEvent, CustomInventory) -> Unit) = action@{ it: GUIClickEvent, inv: CustomInventory ->
        val item = it.item
        val player = it.player
        // TODO Filter & co
    }

    private constructor(builder: Builder) : this(
        buildMap {
            putAll(builder.markableItems)
            builder.items.forEach { put(it, false) }
        },
        buildList {
            addAll(builder.headers)
            builder.header?.let { add(it) }
        },
        builder.filterable,
        builder.filterName,
        builder.scrollable,
        buildList {
            addAll(builder.players)
            builder.player?.let { add(it) }
        },
        builder.title,
        builder.id,
        builder.clickAction,
        builder.closeAction
    )

    class Builder(val id: String) : CustomInventory.Builder() {
        /**
         * Import items that are markable. Marked items will be displayed with either an enchanting glint or and be replaced with a shiny green glass pane, if they not support enchanting glints.
         * @see items
         */
        var markableItems: Map<ItemStack, Boolean> = emptyMap()

        /**
         * Import items to the storage GUI.
         * @see markableItems
         */
        var items: List<ItemStack> = emptyList()

        /**
         * Decorate the storage header (first row) with custom items. You can set
         * - 0 Items for no header
         * - 1 Item ----o----
         * - 2 Items ---o-o---
         * - 3 Items --o--o--o--
         * - 4 Items -o-o-o-o-
         * @see header
         */
        var headers: List<ItemStack> = emptyList()

        /**
         * Decorate the storage header (first row) with a custom item. It will be centered
         * @see headers
         */
        var header: ItemStack? = null

        /**
         * Configure the GUI as filterable. Filterable storage GUIs will have a placeholder row at the bottom with a centered filter switcher
         *
         * **Default: false**
         * @see filterName
         */
        var filterable: Boolean = false

        /**
         * Set the GUI filter. By default, it is "No Filter".
         *
         * **ONLY works if GUI is filterable!**
         * @see filterName
         */
        var filterName: String? = null

        /**
         * Configure the GUI as scrollable. Scrollable storage GUIs will have up and down arrows to navigate on overflow. On disabled scroll GUIs, overflow will be ignored.
         *
         * **Default: false**
         */
        var scrollable: Boolean = false

        /**
         * Internal use. No need to call it inlined
         */
        fun build() = StorageGUI(this)
    }

    override fun update() {
        //Header
        when (header.size) {
            1 -> setItem(4, header[0])
            2 -> {
                setItem(3, header[0])
                setItem(5, header[1])
            }

            3 -> {
                setItem(2, header[0])
                setItem(4, header[1])
                setItem(6, header[2])
            }

            4 -> {
                setItem(1, header[0])
                setItem(3, header[1])
                setItem(5, header[2])
                setItem(7, header[2])
            }
        }

        //Content
        var counter = 0
        content.forEach { (item, activated) ->
            val finalItem = if (activated) {
                val i = when (item.item) {
                    Items.PLAYER_HEAD, Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CHEST,
                    Items.ENDER_CHEST, Items.TRAPPED_CHEST -> itemStack(Items.LIME_STAINED_GLASS_PANE) { tag = item.tag }

                    else -> item
                }

                i.enchant(Enchantments.MENDING, 1)
                i.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS)
                i
            } else item
            if (((scrollable || filterable) && counter >= 24) || counter >= 30) return
            setItem(9 + counter, finalItem)
            counter++
        }
    }

    private fun fillPlaceholder() {
        val darkHolder = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setName(emptyComponent()) }
        val lightHolder = itemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE) { setName(emptyComponent()) }
        (0..8).forEach { setItem(it, darkHolder) }
        (9..53).forEach { setItem(it, lightHolder) }
        if (scrollable || filterable) (45..53).forEach { setItem(it, darkHolder) }
    }

    init {
        fillPlaceholder()
        update()
    }
}

