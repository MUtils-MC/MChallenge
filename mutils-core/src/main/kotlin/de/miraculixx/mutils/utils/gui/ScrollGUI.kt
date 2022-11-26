package de.miraculixx.mutils.utils.gui

import de.miraculixx.mutils.utils.gui.data.CustomInventory
import de.miraculixx.mutils.utils.gui.event.GUIClickEvent
import de.miraculixx.mutils.utils.gui.event.GUICloseEvent
import de.miraculixx.mutils.utils.gui.item.*
import de.miraculixx.mutils.utils.messages.cError
import de.miraculixx.mutils.utils.messages.cmp
import de.miraculixx.mutils.utils.messages.msg
import de.miraculixx.mutils.utils.messages.msgList
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments


class ScrollGUI(
    private val content: Map<ItemStack, Boolean>,
    override val id: String,
    title: Component,
    players: List<Player>,
    startPage: Int,
    clickEvent: ((GUIClickEvent) -> Unit)?,
    closeEvent: ((GUICloseEvent) -> Unit)?
) : CustomInventory(4 * 9, title, clickEvent, closeEvent) {
    private var page = startPage
    private val arrowRedL = getCustomItem("arrowLeftEnd", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=")
    private val arrowRedR = getCustomItem("arrowRightEnd", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNmZTg4NDVhOGQ1ZTYzNWZiODc3MjhjY2M5Mzg5NWQ0MmI0ZmMyZTZhNTNmMWJhNzhjODQ1MjI1ODIyIn19fQ==")
    private val arrowGreenL = getCustomItem("arrowLeft", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU1MGI3Zjc0ZTllZDc2MzNhYTI3NGVhMzBjYzNkMmU4N2FiYjM2ZDRkMWY0Y2E2MDhjZDQ0NTkwY2NlMGIifX19")
    private val arrowGreenR = getCustomItem("arrowRight", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzMzlmZjJlNTM0MmJhMThiZGM0OGE5OWNjYTY1ZDEyM2NlNzgxZDg3ODI3MmY5ZDk2NGVhZDNiOGFkMzcwIn19fQ==")
    private val activated = msg("gui.general.activated")
    private val deactivated = msg("gui.general.deactivated")

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

    private fun update() {
        val firstIndex = page
        val lastIndex = page + 7

        content.toList().subList(
            firstIndex.coerceIn(0 until content.size),
            lastIndex.coerceIn(0 until content.size)
        ).forEachIndexed { index, data ->
            val item = if (data.second) {
                data.first.enchant(Enchantments.MENDING, 1)
                data.first.addHideFlags(HideFlag.HIDE_ENCHANTS)
                setItem(index + 10, itemStack(Items.LIME_STAINED_GLASS_PANE) {
                    setCustomName(activated)
                })
                data.first
            } else data.first
            setItem(index + 1, item)
        }
    }

    private fun fillPlaceholder() {
        val primaryPlaceholder = itemStack(Items.GRAY_STAINED_GLASS_PANE) { setCustomName(cmp(" ")) }
        val secondaryPlaceholder = itemStack(Items.BLACK_STAINED_GLASS_PANE) { setCustomName(cmp(" ")) }
        val missingSetting = itemStack(Items.BARRIER) { setCustomName(cmp("✖", cError)) }

        repeat(containerSize) {
            setItem(it, primaryPlaceholder)
        }
        listOf(0, 1, 7, 8, 9, 17, 18, 26, 27, 28, 34, 35).forEach { setItem(it, secondaryPlaceholder) }
        (10..16).forEach { setItem(it, missingSetting) }
    }

    private fun getCustomItem(key: String, texture: String): ItemStack {
        return itemStack(Items.PLAYER_HEAD) {
            setSkullTexture(texture)
            setCustomName(msg("gui.general.$key.n"))
            setLore(msgList("gui.general.$key.l"))
        }
    }

    init {

    }
}