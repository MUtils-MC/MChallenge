package de.miraculixx.mcore.gui

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.data.InventoryManager
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mvanilla.extensions.lore
import de.miraculixx.mvanilla.extensions.name
import de.miraculixx.mvanilla.messages.*
import de.miraculixx.mvanilla.gui.Head64
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
import org.bukkit.persistence.PersistentDataType

class ScrollGUI(
    override val itemProvider: ItemProvider?,
    override val id: String,
    title: Component,
    players: List<Player>,
    startPage: Int,
    filterable: Boolean,
    private val dataKeys: List<NamespacedKey>,
    clickEvent: ((InventoryClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((InventoryCloseEvent, CustomInventory) -> Unit)?,
) : CustomInventory(4 * 9, title, clickEvent, closeEvent) {
    private val i = get()
    private var page = startPage
    private val arrowRedL = InventoryUtils.getCustomItem("arrowLeftEnd", 9000, Head64.ARROW_LEFT_RED)
    private val arrowRedR = InventoryUtils.getCustomItem("arrowRightEnd", 9000, Head64.ARROW_RIGHT_RED)
    private val arrowGreenL = InventoryUtils.getCustomItem("arrowLeft", 9001, Head64.ARROW_LEFT_GREEN)
    private val arrowGreenR = InventoryUtils.getCustomItem("arrowRight", 9002, Head64.ARROW_RIGHT_GREEN)
    private val pageIndicator = itemStack(Material.KNOWLEDGE_BOOK) {
        meta {
            lore(msgList("items.general.pageIndicator.l", inline = "<grey>"))
            customModel = 9003
        }
    }
    private val activated = cmp(msgString("common.boolTrue"), cSuccess)
    private val deactivated = cmp(msgString("common.boolFalse"), cError)
    override val defaultClickAction: ((InventoryClickEvent) -> Unit) = action@{
        val item = it.currentItem
        val player = it.whoClicked as Player
        val click = it.click
        if (it.slot == -999) {
            if (click.isLeftClick) {
                if (it.inventory.getItem(35)?.itemMeta?.customModel == 9000) return@action
                page += 1
            } else if (click.isRightClick) {
                if (page == 0) return@action
                else page -= 1
            } else return@action
            player.click()
            update()
            return@action
        }

        when (item?.itemMeta?.customModel) {
            9000 -> {
                it.isCancelled = true
                player.playSound(player, org.bukkit.Sound.BLOCK_STONE_HIT, 1f, 1f)
            }

            9001 -> {
                it.isCancelled = true
                page = (page - if (it.click.isShiftClick) 5
                else 1).coerceAtLeast(0)
                player.click()
                update()
            }

            9002 -> {
                it.isCancelled = true
                page += if (it.click.isShiftClick) 5 else 1
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
                    this.itemProvider = this@ScrollGUI.itemProvider
                    this.scrollable = true
                    this.filterable = filterable
                }
            }
        }
    }

    private constructor(builder: Builder) : this(
        builder.itemProvider,
        builder.id,
        builder.title,
        buildList {
            addAll(builder.players)
            builder.player?.let { add(it) }
        },
        builder.startPage,
        builder.filterable,
        builder.dataKeys,
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
         * Defines the data container keys that should be copied to the signal item
         */
        var dataKeys: List<NamespacedKey> = emptyList()

        /**
         * Pass through the value to [StorageGUI], if the storage view is used
         */
        var filterable: Boolean = false

        /**
         * Internal function
         */
        fun build() = ScrollGUI(this)
    }

    override fun update() {
        val lastIndex = page + 7
        val content = itemProvider?.getBooleanMap(page, lastIndex) ?: emptyMap() // Render only visible items

        // Clean up
        fillPlaceholder(false)

        // Adding Basic Buttons
        i.setItem(27, if (page == 0) arrowRedL else arrowGreenL)
        i.setItem(35, if (content.size < 7) arrowRedR else arrowGreenR)
        pageIndicator.amount = (page + 1).coerceIn(1..64)
        pageIndicator.itemMeta = pageIndicator.itemMeta?.apply { name = ((cmp("Page ${page + 1}", cHighlight))) }
        i.setItem(31, pageIndicator)

        // Adding Content
        content.toList().forEachIndexed { index, data ->
            if (data.second) {
                data.first.addUnsafeEnchantment(Enchantment.MENDING, 1)
                data.first.itemMeta = data.first.itemMeta?.apply { addItemFlags(ItemFlag.HIDE_ENCHANTS) }
            }
            i.setItem(index + 19, itemStack(if (data.second) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    name = (if (data.second) activated else deactivated)
                    val sourceMeta = data.first.itemMeta
                    customModel = sourceMeta?.customModel
                    val dataContainer = sourceMeta?.persistentDataContainer
                    dataKeys.forEach { key ->
                        persistentDataContainer.set(key, PersistentDataType.STRING, dataContainer?.get(key, PersistentDataType.STRING) ?: "")
                    }
                }
            })
            i.setItem(index + 10, data.first)
        }
    }

    private fun fillPlaceholder(full: Boolean) {
        val primaryPlaceholder = itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { name = (cmp(" ")) } }
        val secondaryPlaceholder = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { name = (cmp(" ")) } }
        val missingSetting = itemStack(Material.BARRIER) { meta { name = (cmp("✖", cError)) } }

        if (full) {
            repeat(i.size) { i.setItem(it, primaryPlaceholder) }
            listOf(0, 1, 7, 8, 9, 17, 18, 26, 27, 28, 34, 35).forEach { i.setItem(it, secondaryPlaceholder) }
        } else (19..25).forEach { i.setItem(it, primaryPlaceholder) }
        (10..16).forEach { i.setItem(it, missingSetting) }
    }

    init {
        fillPlaceholder(true)
        update()
        open(players)
    }
}