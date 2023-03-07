package de.miraculixx.mutils.gui

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.SortType
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.meta.SkullMeta

class LibraryGUI(
    override val itemProvider: ItemProvider?,
    override val id: String,
    title: Component,
    players: List<Player>,
    startPage: Int,
    clickEvent: ((InventoryClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((InventoryCloseEvent, CustomInventory) -> Unit)?,
) : CustomInventory(6 * 9, title, clickEvent, closeEvent) {
    private val i = get()
    private var page = startPage
    private var global = true
    private var sort = SortType.LIKED

    private val msgGlobal = msgString("items.lib.global.n")
    private val msgPrivat = msgString("items.lib.privat.n")

    private val itemGlobal = itemStack(Material.PLAYER_HEAD) {
        meta {
            name = cmp(msgGlobal, cHighlight)
            lore(msgList("items.lib.global.l"))
            customModel = 3001
        }
        itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.GLOBE.value)
    }
    private val itemSort = itemStack(Material.HOPPER) {
        meta {
            name = cmp(msgString("items.lib.sort.n"), cHighlight)
            customModel = 3002
        }
    }
    private val itemManageOwn = itemStack(Material.PLAYER_HEAD) {
        meta {
            name = cmp(msgString("items.lib.manage.n"), cHighlight)
            lore(msgList("items.lib.manage.l"))
        }
        itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ENDER_CHEST.value)
    }

    override val defaultClickAction: ((InventoryClickEvent) -> Unit) = action@{

    }

    private constructor(builder: LibraryGUI.Builder) : this(
        builder.itemProvider,
        builder.id,
        builder.title,
        buildList {
            addAll(builder.players)
            builder.player?.let { add(it) }
        },
        builder.startPage,
        builder.clickAction,
        builder.closeAction
    )

    class Builder(val id: String) : CustomInventory.Builder() {
        /**
         * Define the startpage for this GUI. Only items matching the current page are visible.
         *
         * Default: **0**
         */
        var startPage: Int = 0

        /**
         * Internal function
         */
        fun build() = LibraryGUI(this)
    }

    override fun update() {

        // Calculate left menu
        i.setItem(9, itemGlobal)
        itemSort.editMeta {
            it.lore(buildList {
                add(emptyComponent())
                SortType.values().forEach { type ->
                    val bullet = if (type == sort) "■" else "□"
                    add(cmp(" $bullet ") + cmp("items.lib.sort.${type.name}"))
                }
            })
        }
        i.setItem(18, itemSort)
        i.setItem(27, itemManageOwn)

        // Calculate content
        val items = itemProvider?.getItemList((page * 6), 24 + (page * 6))
        items?.forEachIndexed { index, itemStack ->
            val row = index / 6
            val column = (index - (row * 6))
            val slot = 11 + column + (9*row)
            itemStack.editMeta { it.name = cmp("Index: $index $row-$column") }
            i.setItem(slot, itemStack)
        }
    }

    private fun fillPlaceholder(full: Boolean) {
        if (full) {
            val phBlack = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
            (0 until 9 * 6).forEach { slot -> i.setItem(slot, phBlack) }
            val phGrey = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
            setOf(10, 19, 28, 37).forEach { slot -> i.setItem(slot, phGrey) }
            i.setItem(36, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } })
        }
        // 2-7
        val phInvis = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
        (1..4).forEach { row ->
            (2..7).forEach { column ->
                val slot = column + (9 * row)
                i.setItem(slot, phInvis)
            }
        }
    }

    init {
        fillPlaceholder(true)
        update()
        open(players)
    }
}