package de.miraculixx.mutils.modules.utils.reminder

import de.miraculixx.mutils.utils.msg
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ReminderCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.notPlayer"))
            return false
        }
        // reminder 10 minutes <messages>
        if (args.size < 2) {
            sender.sendMessage(msg("command.reminder.help", pre = false))
            return false
        }
        val time = args[0].toIntOrNull()
        val multiplier = when (args[1].lowercase()) {
            "seconds" -> 1
            "minutes" -> 60
            "hours" -> 60*60
            else -> null
        }
        if (time == null || multiplier == null) {
            sender.sendMessage(msg("command.reminder.help", pre = false))
            return false
        }
        val message = if (args.size < 3) null else {
            val s = StringBuilder()
            val l = args.toMutableList()
            repeat(2) { l.removeAt(0) }
            l.forEach {
                s.append(" $it")
            }
            s.toString()
        }
        val duration = time.toLong()*multiplier*20
        Reminder(duration,message,sender)
        sender.sendMessage(msg("command.reminder.new", sender, (duration/20).toString(), message))
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        when {
            args.size < 2 -> list.add("<number>")
            args.size == 2 -> {
                list.add("seconds")
                list.add("minutes")
                list.add("hours")
            }
            else -> list.add("<message>")
        }
        return list
    }
}