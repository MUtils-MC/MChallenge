package de.miraculixx.mchallenge.modules.mods.force.huntDeath

import de.miraculixx.mchallenge.modules.mods.force.huntMob.HuntCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class DeathHuntCommand(private val data: DeathHunt) : TabExecutor, HuntCommand<String> {
    override val typeList = data.allDeathKeys
    override val typeNameList = typeList
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return getTabComplete(args, data.blacklist)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        data.handleCommand(sender, sender.name, args)
        return true
    }

    override fun getType(input: String?) = input
}