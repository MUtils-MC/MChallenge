@file:Suppress("DEPRECATION")

package de.miraculixx.mutils.modules.utils.backpack

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.msg
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class BackPackCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.noPlayer"))
            return false
        }

        if (ModuleManager.isActive(Modules.BACKPACK)) {
            sender.sendMessage(msg("command.backpack.disabled"))
            return false
        }

        val config = ConfigManager.getConfig(Configs.BACKPACK)
        val manager = BackPackManager(config)
        if (args.isEmpty()) {
            if (config.getBoolean("Global Backpack")) sender.openInventory(manager.getBackPack())
            else sender.openInventory(manager.getBackPack(sender))
            sender.playSound(sender.location, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 0.8f)
            return true
        }

        if (sender.hasPermission("command.backpack.open-others")) {
            if (config.getStringList("Backpacks").contains(args[0])) {
                if (args[0] == "Global") sender.openInventory(manager.getBackPack())
                else sender.openInventory(manager.getBackPack(Bukkit.getOfflinePlayer(args[0])))
                sender.playSound(sender.location, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 0.8f)
            }
            return true
        }

        sender.sendMessage(msg("command.backpack.help"))
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        val config = ConfigManager.getConfig(Configs.BACKPACK)
        if (p0 is Player && p0.hasPermission("command.backpack.open-others")) {
            config.getStringList("Backpacks").forEach { list.add(it) }
        }
        return list
    }
}