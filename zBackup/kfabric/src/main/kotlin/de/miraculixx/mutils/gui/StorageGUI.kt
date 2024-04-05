package de.miraculixx.mutils.gui

import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.event.GUICloseEvent
import de.miraculixx.mutils.gui.utils.InventoryUtils
import de.miraculixx.mutils.gui.utils.InventoryUtils.clone
import de.miraculixx.mutils.gui.utils.InventoryUtils.getID
import de.miraculixx.mutils.gui.utils.InventoryUtils.setID
import de.miraculixx.mutils.gui.utils.adv
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.toMap
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.nbt.set

class StorageGUI(
    override val itemProvider: ItemProvider?,
    private val header: List<ItemStack>,
    private val filterable: Boolean,
    filterName: String?,
    private val scrollable: Boolean,
    players: List<Player>,
    title: Component,
    override val id: String,
    clickEvent: ((GUIClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((GUICloseEvent, CustomInventory) -> Unit)?
) : CustomInventory(6 * 9, title, clickEvent, closeEvent) {
    private var page: Int = 0
    private val arrowUpRed = if (scrollable) InventoryUtils.getCustomItem("arrowUpRed", 9000, Head64.ARROW_UP_RED) else null
    private val arrowDownRed = if (scrollable) InventoryUtils.getCustomItem("arrowDownRed", 9000, Head64.ARROW_DOWN_RED) else null
    private val arrowUpGreen = if (scrollable) InventoryUtils.getCustomItem("arrowUpGreen", 9001, Head64.ARROW_UP_GREEN) else null
    private val arrowDownGreen = if (scrollable) InventoryUtils.getCustomItem("arrowDownGreen", 9002, Head64.ARROW_DOWN_GREEN) else null
    override val defaultClickAction: ((GUIClickEvent, CustomInventory) -> Unit) = action@{ it: GUIClickEvent, inv: CustomInventory ->
        val item = it.item
        val player = it.player
        when (item.getID()) {
            9000 -> {
                it.isCancelled = true
                player.adv().playSound(Sound.sound(Key.key("block.stone.hit"), Sound.Source.BLOCK, 1f, 1f))
            }

            9001 -> {
                it.isCancelled = true
                page = (page - if (it.click.isShiftClick()) 3 else 1).coerceAtLeast(0)
                player.adv().click()
                update()
            }

            9002 -> {
                it.isCancelled = true
                page += if (it.click.isShiftClick()) 3 else 1
                player.adv().click()
                update()
            }
        }
    }
    private val i = get()
    private val lightHolder = itemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE) { setName(emptyComponent()) }

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

    class Builder(val id: String) : CustomInventory.Builder() {
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
        val content = itemProvider?.getItemList()?.toMap(false)?.plus(itemProvider.getBooleanMap(0, 99)) ?: emptyMap()
        val filter = "NONE"
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
            i.setItem(49, itemStack(Items.HOPPER) {
                setID(9005)
                setName(cmp("Filters", cHighlight, bold = true))
                setLore(
                    listOf(
                        emptyComponent(),
                        cmp("Filter", cHighlight, underlined = true),
                        cmp("∙ ${filter ?: msgString("common.none")}"),
                        emptyComponent(),
                        cmp("Click ", cHighlight) + cmp("≫ Change Filter")
                    )
                )
                getOrCreateTagElement(namespace)["gui.storage.filter"] = filter
            })
        }


        //Visible Content
        val visible = if (scrollable) {
//            println("Content - ${page * 9} -> ${page * 9 + (if (filterable) 9*4 else 9*5)}")
            content.toList().subList(
                (page * 9).coerceIn(0 until content.size),
                (page * 9 + (if (filterable) 9 * 4 else 9 * 5)).coerceIn(0 until content.size + 1)
            )
        } else content.toList()

        //Scroll Apply
        if (scrollable) {
            i.setItem(0, if (page <= 0) arrowUpRed else arrowUpGreen)
            i.setItem(8, if (visible.size < 9 * 3) arrowDownRed else arrowDownGreen)
        }

        //Place Content
        visible.forEachIndexed { index, pair ->
            val finalItem = if (pair.second) {
                val currentItem = when (pair.first.item) {
                    Items.PLAYER_HEAD, Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CHEST,
                    Items.ENDER_CHEST, Items.TRAPPED_CHEST -> pair.first.clone(Items.LIME_STAINED_GLASS_PANE)

                    else -> pair.first
                }

                currentItem.enchant(Enchantments.MENDING, 1)
                currentItem.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS)
                currentItem
            } else pair.first
            if (filterable && index >= 9 * 4) return
            i.setItem(9 + index, finalItem)
        }
    }

    private fun fillPlaceholder(full: Boolean) {
        val darkHolder = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setName(emptyComponent()) }
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

