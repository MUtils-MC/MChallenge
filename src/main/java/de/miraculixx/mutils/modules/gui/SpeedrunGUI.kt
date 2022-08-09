package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import net.axay.kspigot.items.customModel
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class SpeedrunGUI(private val e: InventoryClickEvent, private val p: Player) {
    init {
        event()
    }

    fun event() {
        val c = ConfigManager.getConfig(Configs.SPEEDRUN)
        val tool = GUITools(c)
        val item = e.currentItem

        val cl = e.click
        when (item?.itemMeta?.customModel ?: 0) {
            1 -> when (cl) {
                ClickType.LEFT -> tool.toggleSetting(p, "Village Spawn")
                ClickType.SHIFT_LEFT -> tool.toggleSetting(p, "Village Teleport")
                else -> tool.numberChangerShift(p, cl, "Village Radius", 50, 50, 1000)
            }
            2 -> if (cl == ClickType.LEFT)
                tool.toggleSetting(p, "Portal Spawn")
            else tool.numberChangerShift(p, cl, "Portal Radius", 50, 50, 1000)
            3 -> if (cl == ClickType.LEFT)
                tool.toggleSetting(p, "Timer")
            else tool.numberChangerShift(p, cl, "Timer Delay", 1, 0, 30)
            4 -> tool.toggleSetting(p, "Old Trading")
            5 -> tool.toggleSetting(p, "Disable Brutes")
        }
        GUIBuilder(p, GUI.SPEEDRUN_SETTINGS).scroll(0).open()
    }
}