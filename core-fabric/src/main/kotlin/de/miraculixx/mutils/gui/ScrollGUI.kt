package de.miraculixx.mutils.gui

import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIClick
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.event.GUICloseEvent
import de.miraculixx.mutils.gui.item.*
import de.miraculixx.mutils.gui.utils.adv
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction.setLore
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setSkullTexture
import net.silkmc.silk.nbt.dsl.nbtCompound


class ScrollGUI(
    private val content: Map<ItemStack, Boolean>,
    override val id: String,
    title: Component,
    players: List<Player>,
    startPage: Int,
    clickEvent: ((GUIClickEvent, CustomInventory) -> Unit)?,
    closeEvent: ((GUICloseEvent, CustomInventory) -> Unit)?
) : CustomInventory(4 * 9, title, clickEvent, closeEvent) {
    private var page = startPage
    private val arrowRedL = getCustomItem("arrowLeftEnd", 9000,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=")
    private val arrowRedR = getCustomItem("arrowRightEnd", 9000, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNmZTg4NDVhOGQ1ZTYzNWZiODc3MjhjY2M5Mzg5NWQ0MmI0ZmMyZTZhNTNmMWJhNzhjODQ1MjI1ODIyIn19fQ==")
    private val arrowGreenL = getCustomItem("arrowLeft", 9001,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU1MGI3Zjc0ZTllZDc2MzNhYTI3NGVhMzBjYzNkMmU4N2FiYjM2ZDRkMWY0Y2E2MDhjZDQ0NTkwY2NlMGIifX19")
    private val arrowGreenR = getCustomItem("arrowRight", 9002, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzMzlmZjJlNTM0MmJhMThiZGM0OGE5OWNjYTY1ZDEyM2NlNzgxZDg3ODI3MmY5ZDk2NGVhZDNiOGFkMzcwIn19fQ==")
    private val pageIndicator = itemStack(Items.KNOWLEDGE_BOOK) {
        setLore(msgList("gui.general.pageIndicator.l"))
        addTagElement(namespace, nbtCompound { put("ID", 9003) })
    }
    private val activated = msg("gui.general.activated")
    private val deactivated = msg("gui.general.deactivated")
    override val defaultClickAction: ((GUIClickEvent, CustomInventory) -> Unit) = action@{ it: GUIClickEvent, inv: CustomInventory ->
        val item = it.item
        val player = it.player
        when (item.getTagElement(namespace)?.getInt("ID")) {
            9000 -> {
                it.isCancelled = true
                player.playSound(SoundEvents.STONE_HIT)
            }
            9001 -> {
                it.isCancelled = true
                page -= (if (it.click == GUIClick.SHIFT_RIGHT_CLICK || it.click == GUIClick.SHIFT_LEFT_CLICK) 5
                else 1).coerceAtMost(content.size - 6)
                player.adv().click()
                update()
            }
            9002 -> {
                it.isCancelled = true
                page += (if (it.click == GUIClick.SHIFT_RIGHT_CLICK || it.click == GUIClick.SHIFT_LEFT_CLICK) 5
                else 1).coerceAtLeast(0)
                update()
            }
            9003 -> {
                it.isCancelled = true
                player.adv().click()
                InventoryManager.storageBuilder("$id-STORAGE") {
                    this.title = title
                    this.players = viewers.keys.toList()
                    this.clickAction = clickEvent
                    this.closeAction = closeEvent
                    this.markableItems = content
                    this.scrollable = page > 36
                }
            }
        }
    }

    private constructor(builder: Builder): this(
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

    class Builder(val id: String): CustomInventory.Builder() {
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

    override fun update() {
        val firstIndex = page
        val lastIndex = page + 7

        // Adding Basic Buttons
        setItem(27, if (page == 0) arrowRedL else arrowGreenL)
        setItem(35, if (page >= content.size - 6) arrowRedR else arrowRedL)
        pageIndicator.count = page
        pageIndicator.setName(cmp("Page $page", cHighlight))
        setItem(31, pageIndicator)

        // Adding Content
        content.toList().subList(
            firstIndex.coerceIn(0 until content.size),
            lastIndex.coerceIn(0 until content.size)
        ).forEachIndexed { index, data ->
            val item = if (data.second) {
                data.first.enchant(Enchantments.MENDING, 1)
                data.first.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS)
                data.first
            } else data.first
            setItem(index + 10, itemStack(if (data.second) Items.LIME_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE) {
                setName(if (data.second) activated else deactivated)
                addTagElement(namespace, nbtCompound { put("ID", item.getTagElement(namespace)?.getInt("ID") ?: 0) })
            })
            setItem(index + 1, item)
        }
    }

    private fun fillPlaceholder() {
        val primaryPlaceholder = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setName(cmp(" ")) }
        val secondaryPlaceholder = itemStack(Items.BLACK_STAINED_GLASS_PANE) { setName(cmp(" ")) }
        val missingSetting = itemStack(Items.BARRIER) { setName(cmp("✖", cError)) }

        repeat(containerSize) {
            setItem(it, primaryPlaceholder)
        }
        listOf(0, 1, 7, 8, 9, 17, 18, 26, 27, 28, 34, 35).forEach { setItem(it, secondaryPlaceholder) }
        (10..16).forEach { setItem(it, missingSetting) }
    }

    private fun getCustomItem(key: String, id: Int, texture: String): ItemStack {
        return itemStack(Items.PLAYER_HEAD) {
            setSkullTexture(texture)
            setName(msg("gui.general.$key.n"))
            setLore(msgList("gui.general.$key.l"))
            addTagElement(namespace, nbtCompound { put("ID", id) })
        }
    }

    init {
        if (players.isEmpty()) {
            consoleAudience.sendMessage(prefix + cmp("Creating GUI without player - Unexpected behaviour", cError))
            InventoryManager.remove(id)
        } else {
            fillPlaceholder()
            update()
            open(players)
        }
    }
}