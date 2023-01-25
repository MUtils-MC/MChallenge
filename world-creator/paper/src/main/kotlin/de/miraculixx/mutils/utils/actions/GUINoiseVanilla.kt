package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.data.GeneratorDefaults
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.soundDisable
import de.miraculixx.mutils.extensions.soundEnable
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUINoiseVanilla(defaults: GeneratorDefaults, oldInv: CustomInventory) : GUIEvent {
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
                oldInv.update()
                oldInv.open(player)
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