package de.miraculixx.mutils.commands

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.items.ItemLib
import net.axay.kspigot.event.listen
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class TestInventory: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val player = sender as? Player ?: return false

        val size = args?.getOrNull(0)?.toIntOrNull() ?: 4
        GUIBuilder(player, GUI.CUSTOM).custom(false, getMap(size), size, "§9TEST").open()
        return true
    }

    private val onClick = listen<InventoryClickEvent> {
        val player = it.whoClicked as? Player ?: return@listen
        if (it.view.title != "§9TEST") return@listen
        val size = it.inventory.size / 9
        val slot = it.slot
        it.isCancelled = true

        val tools = ItemLib()
        when {
            slot in 3..5 -> GUIBuilder(player, GUI.CUSTOM, GUIAnimation.MOVE_DOWN).custom(true, tools.getMain(1), size, it.view.title).open()
            slot % 9 == 0 -> GUIBuilder(player, GUI.CUSTOM, GUIAnimation.MOVE_RIGHT).custom(true, tools.getMain(1), size, it.view.title).open()
            (slot + 1) % 9 == 0 -> GUIBuilder(player, GUI.CUSTOM, GUIAnimation.MOVE_LEFT).custom(true, tools.getMain(1), size, it.view.title).open()
            slot in (size*9 - 5)..(size*9 - 3) -> GUIBuilder(player, GUI.CUSTOM, GUIAnimation.MOVE_UP).custom(true, tools.getMain(1), size, it.view.title).open()
        }
    }

    private fun getMap(size: Int): Map<ItemStack, Int> {
        return buildMap {
            repeat(size * 9) {
                put(itemStack(Material.RED_STAINED_GLASS_PANE) { meta {
                    amount = it.plus(1).coerceIn(0..64)
                    name = "${it.plus(1)}"
                }}, it)
            }
        }
    }
}