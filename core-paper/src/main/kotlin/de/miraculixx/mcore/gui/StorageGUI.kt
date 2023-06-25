package de.miraculixx.mcore.gui

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.items.ItemFilterProvider
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.toMap
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class StorageGUI(
    override val itemProvider: ItemProvider?,
    private val header: List<ItemStack>,
    private val filterable: Boolean,
    filterName: String?,
    private val scrollable: Boolean,
    players: List<Player>,
    title: Component,
    override val id: String,
    clickEvent: ((InventoryClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((InventoryCloseEvent, CustomInventory) -> Unit)?
) : CustomInventory(6 * 9, title, clickEvent, closeEvent) {
    private var page: Int = 0
    private val arrowUpRed = if (scrollable) InventoryUtils.getCustomItem("arrowUpRed", 9000, Head64.ARROW_UP_RED) else null
    private val arrowDownRed = if (scrollable) InventoryUtils.getCustomItem("arrowDownRed", 9000, Head64.ARROW_DOWN_RED) else null
    private val arrowUpGreen = if (scrollable) InventoryUtils.getCustomItem("arrowUpGreen", 9001, Head64.ARROW_UP_GREEN) else null
    private val arrowDownGreen = if (scrollable) InventoryUtils.getCustomItem("arrowDownGreen", 9002, Head64.ARROW_DOWN_GREEN) else null
    override val defaultClickAction: ((InventoryClickEvent) -> Unit) = action@{
        val item = it.currentItem
        val player = it.whoClicked
        when (item?.itemMeta?.customModel) {
            9000 -> {
                it.isCancelled = true
                player.playSound(Sound.sound(Key.key("block.stone.hit"), Sound.Source.BLOCK, 1f, 1f))
            }
            9001 -> {
                it.isCancelled = true
                page = (page - if (it.click.isShiftClick) 3 else 1).coerceAtLeast(0)
                player.click()
                update()
            }
            9002 -> {
                it.isCancelled = true
                page += if (it.click.isShiftClick) 3 else 1
                player.click()
                update()
            }
        }
    }
    private val i = get()
    private val lightHolder = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) { meta { displayName(emptyComponent()) } }

    private constructor(builder: Builder) : this(
        builder.itemProvider,
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

    class Builder(val id: String): CustomInventory.Builder() {
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
        val from = page * 9
        val to = (from + (9 * 5)) - (if (filterable) 9 else 0)
        val content = itemProvider?.getItemList(from, to)?.toMap(false)?.plus(itemProvider.getBooleanMap(from, to)) ?: emptyMap()
        val filter = (itemProvider as? ItemFilterProvider)?.filter
        fillPlaceholder(false)

        //Header
        when (header.size) {
            1 -> i.setItem(4, header[0])
            2 -> {
                i.setItem(3, header[0])
                i.setItem(5, header[1])
            }

            3 -> {
                i.setItem(2, header[0])
                i.setItem(4, header[1])
                i.setItem(6, header[2])
            }

            4 -> {
                i.setItem(1, header[0])
                i.setItem(3, header[1])
                i.setItem(5, header[2])
                i.setItem(7, header[2])
            }
        }

        //Filter Apply
        if (filterable) {
            i.setItem(49, itemStack(Material.HOPPER) { meta {
                customModel = 9005
                displayName(cmp("Filters", cHighlight, bold = true))
                lore(listOf(
                    emptyComponent(),
                    cmp("Filter", cHighlight, underlined = true),
                    cmp("∙ ${filter ?: msgString("common.none")}"),
                    emptyComponent(),
                    cmp("Click ", cHighlight) + cmp("≫ Change Filter")
                ))
                persistentDataContainer.set(NamespacedKey(namespace, "gui.storage.filter"), PersistentDataType.STRING, (filter ?: "NO_FILTER"))
            }})
        }



        //Visible Content
        val visible = content.toList()

        //Scroll Apply
        if (scrollable) {
            i.setItem(0, if (page <= 0) arrowUpRed else arrowUpGreen)
            i.setItem(8, if (visible.size < 9*3) arrowDownRed else arrowDownGreen)
        }

        //Place Content
        visible.forEachIndexed { index, pair ->
            if (pair.second) {
                 when (pair.first.type) {
                    Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.CHEST,
                    Material.ENDER_CHEST, Material.TRAPPED_CHEST -> pair.first.type = Material.GREEN_STAINED_GLASS_PANE
                     else -> Unit
                }

                pair.first.addUnsafeEnchantment(Enchantment.MENDING, 1)
                pair.first.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
            if (filterable && index >= 9*4) return
            i.setItem(9 + index, pair.first)
        }
    }

    private fun fillPlaceholder(full: Boolean) {
        val darkHolder = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(emptyComponent()) } }
        if (full) (0..8).forEach { i.setItem(it, darkHolder) }

        (9..53 - (if (filterable) 9 else 0)).forEach { i.setItem(it, lightHolder) }
        if (filterable) (45..53).forEach { i.setItem(it, darkHolder) }
    }

    init {
        fillPlaceholder(true)
        update()
        open(players)
    }
}

