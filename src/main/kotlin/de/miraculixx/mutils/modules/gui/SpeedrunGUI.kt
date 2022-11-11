package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
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
        val item = e.currentItem

        val cl = e.click
        when (item?.itemMeta?.customModel ?: 0) {
            1 -> when (cl) {
                ClickType.LEFT -> InvUtils.toggleSetting(c, p, "Village Spawn")
                ClickType.SHIFT_LEFT -> InvUtils.toggleSetting(c, p, "Village Teleport")
                else -> InvUtils.numberChangerShift(c, p, cl, "Village Radius", 50, 50, 1000)
            }
            2 -> if (cl == ClickType.LEFT)
                InvUtils.toggleSetting(c, p, "Portal Spawn")
            else InvUtils.numberChangerShift(c, p, cl, "Portal Radius", 50, 50, 1000)
            3 -> if (cl == ClickType.LEFT)
                InvUtils.toggleSetting(c, p, "Timer")
            else InvUtils.numberChangerShift(c, p, cl, "Timer Delay", 1, 0, 30)
            4 -> InvUtils.toggleSetting(c, p, "Old Trading")
            5 -> InvUtils.toggleSetting(c, p, "Disable Brutes")
        }
        GUIBuilder(p, GUI.SPEEDRUN_SETTINGS).scroll(0).open()
    }
}