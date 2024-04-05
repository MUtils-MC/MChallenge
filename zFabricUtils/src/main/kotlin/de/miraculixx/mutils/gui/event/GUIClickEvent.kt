package de.miraculixx.mutils.gui.event

import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIClick
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class GUIClickEvent(
    val gui: CustomInventory,
    val player: Player,
    val slot: Int,
    val item: ItemStack,
    val click: GUIClick,
) {
    /**
     * Set it to true to prevent item interaction. If [isCancelled] is already true from beginning, the clicked item is a menu button handled by the GUI itself
     */
    var isCancelled = false
}