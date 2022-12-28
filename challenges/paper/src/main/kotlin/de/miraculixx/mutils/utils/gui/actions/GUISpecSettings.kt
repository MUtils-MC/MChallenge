package de.miraculixx.mutils.utils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.enums.spectator.Activation
import de.miraculixx.mutils.enums.spectator.Visibility
import de.miraculixx.mutils.extensions.soundDisable
import de.miraculixx.mutils.extensions.soundEnable
import de.miraculixx.mutils.extensions.soundStone
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.modules.spectator.SpecCollection
import de.miraculixx.mutils.modules.spectator.Spectator
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class GUISpecSettings(settings: SpecCollection): GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        val player = it.whoClicked as? Player ?: return@event
        val uuid = player.uniqueId
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel) {
            1 -> settings.hide = if (settings.hide == Visibility.HIDDEN) {
                player.soundEnable()
                Spectator.removeHide(uuid)
                Spectator.performReveal(player)
                Visibility.SHOWN
            } else {
                player.soundDisable()
                Spectator.addHide(uuid)
                Spectator.performHide(player)
                Visibility.HIDDEN
            }

            2 -> settings.selfHide = if (settings.selfHide == Visibility.HIDDEN) {
                player.soundEnable()
                Spectator.performSelfReveal(player)
                Visibility.SHOWN
            } else {
                player.soundDisable()
                Spectator.performSelfHide(player)
                Visibility.HIDDEN
            }

            3 -> {
                val current = settings.flySpeed
                val click = it.click
                settings.flySpeed = if (click == ClickType.RIGHT) {
                    if (current <= -10) {
                        player.soundStone()
                        return@event
                    }
                    current - 1
                } else {
                    if (current >= 10) {
                        player.soundStone()
                        return@event
                    }
                    current + 1
                }
            }

            4 -> settings.itemPickup = player.toggle(settings.itemPickup)
            5 -> settings.blockBreak = player.toggle(settings.blockBreak)
        }
        inv.update()
    }

    private fun Player.toggle(current: Activation): Activation {
        return if (current == Activation.DISABLED) {
            soundEnable()
            Activation.ENABLED
        } else {
            soundDisable()
            Activation.DISABLED
        }
    }
}