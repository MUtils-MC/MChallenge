package de.miraculixx.mchallenge.modules.mods.limitedSkills

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class LimitGUI(private val challengeInstance: LimitedSkills): GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel) {
            1 -> setPlayer(player, true, inv)
            2 -> setPlayer(player, false, inv)
            3 -> {
                player.click()
                onlinePlayers.forEach { p -> p.playSound(p, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.2f) }
                inv.close()
                challengeInstance.startGame()
            }
        }
    }

    private fun setPlayer(player: Player, state: Boolean, inventory: CustomInventory) {
        challengeInstance.selection[player.uniqueId] = state
        player.click()
        inventory.update()
    }

    override val close: ((InventoryCloseEvent, CustomInventory) -> Unit) = event@{ it: InventoryCloseEvent, inv: CustomInventory ->
        if (it.reason == InventoryCloseEvent.Reason.PLUGIN) return@event
        taskRunLater(3) { inv.open(it.player as Player) }
    }
}