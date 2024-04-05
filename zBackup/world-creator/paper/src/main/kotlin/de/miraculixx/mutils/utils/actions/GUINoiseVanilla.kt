package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.challenge.api.data.GeneratorDefaults
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.extensions.soundEnable
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUINoiseVanilla(defaults: GeneratorDefaults, previousInv: CustomInventory) : GUIEvent {
    override val close: ((InventoryCloseEvent, CustomInventory) -> Unit) = event@{ it: InventoryCloseEvent, _: CustomInventory ->
        if (it.reason == InventoryCloseEvent.Reason.PLAYER) taskRunLater(1) {
            previousInv.update()
            previousInv.open(it.player as? Player ?: return@taskRunLater)
        }
    }

    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel) {
            1 -> defaults.vanillaNoise = defaults.vanillaNoise.toggle(player)
            2 -> defaults.vanillaCaves = defaults.vanillaCaves.toggle(player)
            3 -> defaults.vanillaSurface = defaults.vanillaSurface.toggle(player)
            4 -> defaults.vanillaFoliage = defaults.vanillaFoliage.toggle(player)
            5 -> defaults.vanillaMobs = defaults.vanillaMobs.toggle(player)

            null, 0 -> {
                previousInv.update()
                previousInv.open(player)
                player.click()
                return@event
            }
        }
        inv.update()
    }

    private fun Boolean.toggle(player: Player): Boolean {
        return if (this) {
            player.soundDisable()
            false
        } else {
            player.soundEnable()
            true
        }
    }
}