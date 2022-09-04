package de.miraculixx.mutils.modules.speedrun

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.worlds
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SpeedrunCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.notPlayer"))
            return false
        }
        if (args.isEmpty()) {
            GUIBuilder(sender, GUI.SPEEDRUN_SETTINGS, GUIAnimation.WATERFALL_OPEN).scroll(0).open()
            return false
        }
        val manager = PrepareWorld()
        when (args[0].lowercase()) {
            "confirm" -> {
                if (challenges != ChallengeStatus.STOPPED) {
                    sender.sendMessage(msg("module.global.challengeActive"))
                    return false
                }
                if (ModuleManager.isActive(Modules.SPEEDRUN)) {
                    sender.sendMessage(msg("command.speedrun.isActive"))
                    return false
                }
                if (!ModuleManager.isActive(Modules.TIMER))
                    ModuleManager.enableModule(Modules.TIMER)
                ModuleManager.enableModule(Modules.SPEEDRUN)
                manager.newWorld(ConfigManager.getConfig(Configs.SPEEDRUN))
            }
            "enable" -> {
                if (ModuleManager.isActive(Modules.SPEEDRUN)) {
                    sender.sendMessage(msg("command.speedrun.isActive"))
                    return false
                }
                sender.playSound(sender.location, Sound.ENTITY_ENDER_DRAGON_GROWL,1f,1.2f)
                //TODO
                sender.sendMessage(msg("modules.speedrun.warning", sender))
            }
            "disable" -> {
                if (!ModuleManager.isActive(Modules.SPEEDRUN)) {
                    sender.sendMessage(msg("command.speedrun.isDisabled"))
                    return false
                }
                ModuleManager.disableModule(Modules.SPEEDRUN)
                worlds.forEach {
                    if (it.name.contains("speedrun"))
                        manager.deleteWorld(it)
                }
                sender.sendMessage(msg("command.speedrun.disable"))
            }
            "settings" -> GUIBuilder(sender, GUI.SPEEDRUN_SETTINGS, GUIAnimation.WATERFALL_OPEN).scroll(0).open()

            else -> sender.sendMessage(msg("command.speedrun.help", pre = false))
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (args.size < 2) {
            list.add("disable")
            list.add("enable")
            list.add("settings")
        }
        return list
    }
}