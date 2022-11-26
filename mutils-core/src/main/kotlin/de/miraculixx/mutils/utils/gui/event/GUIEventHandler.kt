package de.miraculixx.mutils.utils.gui.event

import de.miraculixx.mutils.utils.gui.data.CustomInventory
import de.miraculixx.mutils.utils.gui.data.GUIClick
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType

class GUIEventHandler(
    type: MenuType<*>,
    syncId: Int,
    playerInv: Inventory,
    private val customInv: CustomInventory,
    height: Int,
    private val clickEvent: ((GUIClickEvent) -> Unit)?,
    private val closeEvent: ((GUICloseEvent) -> Unit)?,
    private val defaultClickAction: ((GUIClickEvent) -> Unit)?
): ChestMenu(type, syncId, playerInv, customInv, height) {
    override fun clicked(slot: Int, click: Int, clickType: ClickType, player: Player) {
        val event = GUIClickEvent(customInv, player, slot, container.getItem(slot), GUIClick.fromSlotActionType(clickType, click))
        defaultClickAction?.invoke(event)
        clickEvent?.invoke(event)
        if (event.isCancelled) sendAllDataToRemote()
        else super.clicked(slot, click, clickType, player)
    }

    override fun removed(player: Player) {
        val event = GUICloseEvent(customInv, player)
        closeEvent?.invoke(event)
    }
}