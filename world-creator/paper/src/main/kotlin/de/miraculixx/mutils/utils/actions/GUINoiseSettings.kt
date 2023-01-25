package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.data.GeneratorData
import de.miraculixx.mutils.data.enums.AlgorithmSettingIndex
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.module.MapRender
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class GUINoiseSettings(generatorData: GeneratorData, mainInv: CustomInventory) : GUIEvent {
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
                    AlgorithmSettingIndex.X1, AlgorithmSettingIndex.X2, AlgorithmSettingIndex.X3 -> setting.numberChange(click, generatorData, player)
                    AlgorithmSettingIndex.MODE, AlgorithmSettingIndex.RND, AlgorithmSettingIndex.INVERT -> setting.boolChanger(generatorData, player)
                }
                inv.update()
            }

            2 -> {
                player.click()
                MapRender(player, inv, listOf(generatorData))
            }

            else -> {
                mainInv.update()
                mainInv.open(player)
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