package de.miraculixx.mchallenge.gui.actions

import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mchallenge.utils.config.ConfigManager
import de.miraculixx.mcommons.extensions.click
import de.miraculixx.mcommons.extensions.nextValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ActionSettings(private val preInventory: CustomInventory) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val item = it.currentItem?.itemMeta ?: return@event
        val player = it.whoClicked as? Player ?: return@event

        when (item.customModel) {
            1 -> {

            }

            2 -> {
                val settings = ConfigManager.settings.gui
                settings.compact = !settings.compact
                inv.update()
            }

            3 -> {
                val settings = ConfigManager.settings
                val localization = ConfigManager.localization
                settings.language = localization.getLoadedKeys().toTypedArray().nextValue(settings.language)
                localization.setLanguage(settings.language)
                inv.update()
            }

            else -> {
                preInventory.update()
                preInventory.open(player)
                CoroutineScope(Dispatchers.Default).launch { ConfigManager.save() }
            }
        }
        player.click()
    }
}