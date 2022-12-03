package de.miraculixx.mutils.gui

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


class ScrollGUI(
    private val content: Map<ItemStack, Boolean>,
    override val id: String,
    title: Component,
    players: List<Player>,
    startPage: Int,
    clickEvent: ((InventoryClickEvent) -> Unit)?,
    closeEvent: ((InventoryCloseEvent) -> Unit)?,
) : CustomInventory(4 * 9, title, clickEvent, closeEvent) {
    private val i = get()
    private var page = startPage
    private val arrowRedL = InventoryUtils.getCustomItem("arrowLeftEnd", 9000, Head64.ARROW_LEFT_RED)
    private val arrowRedR = InventoryUtils.getCustomItem("arrowRightEnd", 9000, Head64.ARROW_RIGHT_RED)
    private val arrowGreenL = InventoryUtils.getCustomItem("arrowLeft", 9001, Head64.ARROW_LEFT_GREEN)
    private val arrowGreenR = InventoryUtils.getCustomItem("arrowRight", 9002, Head64.ARROW_RIGHT_GREEN)
    private val pageIndicator = itemStack(Material.KNOWLEDGE_BOOK) {
        meta {
            lore(msgList("gui.general.pageIndicator.l"))
            customModel = 9003
        }
    }
    private val activated = msg("gui.general.activated")
    private val deactivated = msg("gui.general.deactivated")
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
                page -= (if (it.click.isShiftClick) 5
                else 1).coerceAtMost(content.size - 6)
                player.click()
                update()
            }

            9002 -> {
                it.isCancelled = true
                page += (if (it.click.isShiftClick) 5
                else 1).coerceAtLeast(0)
                player.click()
                update()
            }

            9003 -> {
                it.isCancelled = true
                player.click()
                InventoryManager.storageBuilder("$id-STORAGE") {
                    this.title = title
                    this.players = viewers
                    this.clickAction = clickEvent
                    this.closeAction = closeEvent
                    this.markableItems = content
                    this.scrollable = page > 36
                }
            }
        }
    }

    private constructor(builder: Builder) : this(
        builder.content,
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
         * Import items to the scroll menu. All items will be placed in one row and displayed if the current page contains them.
         * [ItemStack] with true import ([Boolean]) will be marked as activated (green glass pain).
         */
        var content: Map<ItemStack, Boolean> = emptyMap()

        /**
         * Define the startpage for this GUI. Only items matching the current page are visible.
         *
         * Default: **0**
         */
        var startPage: Int = 0

        /**
         * Internal function
         */
        fun build() = ScrollGUI(this)
    }

    private fun update() {
        val firstIndex = page
        val lastIndex = page + 7

        // Adding Basic Buttons
        i.setItem(27, if (page == 0) arrowRedL else arrowGreenL)
        i.setItem(35, if (page >= content.size - 6) arrowRedR else arrowGreenR)
        pageIndicator.amount = page.coerceIn(1..64)
        pageIndicator.editMeta { it.displayName((cmp("Page $page", cHighlight))) }
        i.setItem(31, pageIndicator)

        // Adding Content
        content.toList().subList(
            firstIndex.coerceIn(0 until content.size),
            lastIndex.coerceIn(0 until content.size)
        ).forEachIndexed { index, data ->
            if (data.second) {
                data.first.addEnchantment(Enchantment.MENDING, 1)
                data.first.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
            i.setItem(index + 10, itemStack(if (data.second) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    displayName(if (data.second) activated else deactivated)
                    customModel = data.first.itemMeta.customModel
                }
            })
            i.setItem(index + 1, data.first)
        }
    }

    private fun fillPlaceholder() {
        val primaryPlaceholder = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
        val secondaryPlaceholder = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { displayName(cmp(" ")) } }
        val missingSetting = itemStack(Material.BARRIER) { meta { displayName(cmp("✖", cError)) } }

        repeat(i.size) {
            i.setItem(it, primaryPlaceholder)
        }
        listOf(0, 1, 7, 8, 9, 17, 18, 26, 27, 28, 34, 35).forEach { i.setItem(it, secondaryPlaceholder) }
        (10..16).forEach { i.setItem(it, missingSetting) }
    }

    init {
        fillPlaceholder()
        update()
        open(players)
    }
}