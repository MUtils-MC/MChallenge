package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.commands.tools.CommandTools
import de.miraculixx.mutils.system.config.Config
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

class TexturePack : CommandExecutor, TabCompleter {
    val file = Config("utils/texturepack")

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(msg("command.texturepack.help", pre = false))
            return false
        }

        val config = file.getConfig()
        when (args[0].lowercase(Locale.getDefault())) {
            "set" -> {
                if (args.size < 3) {
                    sender.sendMessage(msg("command.texturepack.help", pre = false))
                    return false
                }
                val name = args[1].lowercase(Locale.getDefault())
                val url = args[2]
                config.set(name,url)
                sender.sendMessage(msg("command.texturepack.set", input = name, input2 = url))
                file.save()
            }
            "load" -> {
                val name = args[1].lowercase(Locale.getDefault())
                val tp = config.getString(name)
                val tools = CommandTools()
                if (tp == null) {
                    sender.sendMessage(msg("command.texturepack.notExist", input = name))
                    return false
                }
                val targets = ArrayList<Entity>()
                if (args.size == 2) {
                    if (sender is Player) targets.add(sender)
                    else {
                        sender.sendMessage(msg("command.noPlayer"))
                        return false
                    }
                } else if (args.size == 3) {
                    if (args[2].startsWith('@')) {
                        tools.selector(sender,args[2]).forEach { targets.add(it) }
                    } else {
                        val player = Bukkit.getPlayer(args[2])
                        if (player == null) {
                            sender.sendMessage(msg("command.notOnline"))
                            return false
                        }
                        targets.add(player)
                    }
                }
                targets.forEach {
                    if (it is Player) {
                        it.sendMessage(msg("command.texturepack.load", input = name, input2 = sender.name))
                        if (it.isOp) it.sendMessage(msg("command.texturepack.loadAdmin", input = name, input2 = sender.name))
                        it.setResourcePack(tp)
                    }
                }
            }
            "delete" -> {
                val name = args[1].lowercase(Locale.getDefault())
                if (!config.isSet(name)) {
                    sender.sendMessage(msg("command.texturepack.notExist", input = name))
                    return false
                }
                config.set(name, null)
                file.save()
                sender.sendMessage(msg("command.texturepack.delete", input = name))
            }
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        when {
            p3.size < 2 -> {
                list.add("set")
                list.add("load")
                list.add("delete")
            }
            p3.size == 2 && p3[1] == "load" -> {
                val config = file.getConfig()
                list.addAll(config.getStringList("Texture Packs"))
            }
            p3.size == 3 && p3[1] == "load" -> {
                onlinePlayers.forEach { list.add(it.name) }
                list.add("@a")
                list.add("@r")
                list.add("@s")
            }
        }
        return list
    }
}