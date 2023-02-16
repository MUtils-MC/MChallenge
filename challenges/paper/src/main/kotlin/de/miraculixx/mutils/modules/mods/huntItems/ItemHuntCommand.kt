package de.miraculixx.mutils.modules.mods.huntItems

import de.miraculixx.mutils.extensions.enumOf
import de.miraculixx.mutils.messages.cError
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import de.miraculixx.mutils.utils.getMaterials
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ItemHuntCommand(private val data: ItemHunt): TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return when (args?.size ?: 0) {
            0, 1 -> listOf("skip", "reset", "blacklist")
            2 -> if (args?.getOrNull(0) == "blacklist") {
                listOf("add", "remove")
            } else emptyList()
            3 -> when (args?.getOrNull(1)) {
                "add" -> getMaterials(true).map { it.name }
                "remove" -> data.getBlacklist().map { it.name }
                else -> emptyList()
            }

            else -> emptyList()
        }.filter { it.contains(args?.lastOrNull() ?: "") }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when (args?.getOrNull(0)?.lowercase()) {
//            "skip" -> data.nextMob(sender.name, sender)
//            "reset" -> data.reset()
            "blacklist" -> {
                val arg3 = args.getOrNull(2)
                val material = enumOf<Material>(arg3)
                if (material == null) {
                    sender.sendMessage(prefix + cmp("Please enter a valid item!", cError))
                    return false
                }

                when (args.getOrNull(1)?.lowercase()) {
                    "add" -> data.addBlacklist(material)
                    "remove" -> data.removeBlacklist(material)
                    else -> sender.sendMessage(prefix + cmp("Invalid command structure", cError))
                }
            }

            else -> sender.sendMessage(prefix + cmp("Invalid command structure", cError))
        }
        return true
    }
}