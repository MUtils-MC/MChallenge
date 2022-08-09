package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.system.config.Config
import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.extensions.broadcast
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class PositionCommand : CommandExecutor, TabCompleter {

    val file = Config("utils/position")
    val config = file.getConfig()

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {

        if (args.isEmpty()) {
            sender.sendMessage(msg("command.position.help", pre = false))
            return true
        }

        if (args[0].equals("list", ignoreCase = true)) {
            val locArray = config.getStringList("Position Names")
            sender.sendMessage(msg("command.position.list", input = locArray.toString()))
            return true
        } else {
            val insert = args[0].lowercase(Locale.getDefault())
            if (config.isSet("Position.$insert")) {
                val output = config.getString("Position.$insert")
                if (args.size == 2 && args[1] == "delete") {
                    config["Position.$insert"] = null
                    val locArray = config.getStringList("Position Names")
                    locArray.remove(insert)
                    config["Position Names"] = locArray
                    sender.sendMessage(msg("command.position.delete", input = insert, input2 = output))
                    return true
                }
                sender.sendMessage(msg("command.position.get", input = insert, input2 = output))
                return true
            } else {
                if (sender !is Player) {
                    sender.sendMessage(msg("command.noPlayer"))
                    return false
                }
                val locArray = config.getStringList("Position Names")
                locArray.add(insert)
                config["Position Names"] = locArray
                config["Position.$insert"] = "${sender.location.blockX} ${sender.location.blockY} ${sender.location.blockZ}"
                broadcast(msg("command.position.set", sender, input = insert, input2 = config.getString("Position.$insert")))
                file.save()
                return true
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (args.size <= 1) {
            config.getStringList("Position Names").forEach {
                list.add(it)
            }
            list.add("list")
        } else if (args.size == 2)
            list.add("delete")

        return list
    }
}