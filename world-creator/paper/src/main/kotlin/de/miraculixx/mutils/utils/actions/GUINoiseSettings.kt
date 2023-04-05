package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.api.data.GeneratorData
import de.miraculixx.api.data.GeneratorProviderData
import de.miraculixx.api.data.enums.AlgorithmSettingIndex
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mvanilla.messages.namespace
import de.miraculixx.mutils.module.MapRender
import de.miraculixx.mvanilla.extensions.*
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.persistence.PersistentDataType

class GUINoiseSettings(generatorSettings: GeneratorData, previousInv: CustomInventory, generatorProviderData: GeneratorProviderData? ) : GUIEvent {
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
        val meta = item.itemMeta

        when (meta.customModel) {
            1 -> {
                val key = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.noise.setting"), PersistentDataType.STRING)
                val setting = enumOf<AlgorithmSettingIndex>(key) ?: return@event
                val click = it.click
                when (setting) {
                    AlgorithmSettingIndex.X1, AlgorithmSettingIndex.X2, AlgorithmSettingIndex.X3 -> setting.numberChange(click, generatorSettings, player)
                    AlgorithmSettingIndex.MODE, AlgorithmSettingIndex.RND, AlgorithmSettingIndex.INVERT -> setting.boolChanger(generatorSettings, player)
                    AlgorithmSettingIndex.KEY -> setting.getString(generatorSettings)
                }
                inv.update()
            }

            2 -> {
                if (generatorProviderData == null) return@event
                player.click()
                MapRender(player, inv, listOf(generatorProviderData))
            }

            else -> {
                previousInv.update()
                previousInv.open(player)
                player.click()
            }
        }
    }

    private fun AlgorithmSettingIndex.boolChanger(generatorData: GeneratorData, player: Player) {
        val current = getBoolean(generatorData) ?: false
        val new = if (current) {
            player.soundDisable()
            false
        } else {
            player.soundEnable()
            true
        }
        set(new, generatorData)
    }

    private fun AlgorithmSettingIndex.numberChange(click: ClickType, generatorData: GeneratorData, player: Player) {
        val current = getInt(generatorData) ?: 0
        val modifier = if (click.isShiftClick) 5 else 1
        val new = when (click) {
            ClickType.RIGHT, ClickType.SHIFT_RIGHT -> if (current <= 0) {
                player.soundError()
                return
            } else {
                player.soundDown()
                (current - modifier).coerceAtLeast(0)
            }

            ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                player.soundUp()
                current + modifier
            }

            else -> return
        }
        set(new, generatorData)
    }
}