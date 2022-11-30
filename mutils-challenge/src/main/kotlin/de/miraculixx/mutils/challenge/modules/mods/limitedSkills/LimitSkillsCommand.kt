@file:Suppress("SpellCheckingInspection")

package de.miraculixx.mutils.modules.challenge.mods.limitedSkills

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.utils.prefix
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.util.*

class LimitSkillsCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        /*
        /limitedskills player1 [user]
        /limitedskills player2 [user]
         */
        if (args.size < 2) {
            sender.sendMessage("$prefix Syntax: §9/ls [player1 - player2] [user]§7\n>> Ein User muss online und ausserhalb des Spec Modes sein!")
            return false
        }

        if (!ModuleManager.isActive(Challenge.LIMITED_SKILLS)) {
            sender.sendMessage("$prefix §cBitte aktiviere zuvor die §9LimitedSkills §7Challenge!")
            return false
        }
        val player = Bukkit.getPlayer(args[1])
        if (player == null) {
            sender.sendMessage("$prefix §cDer angegebene Spieler existiert nicht!")
            return false
        }
        if (!player.isOnline || Spectator.isSpectator(player.uniqueId)) {
            sender.sendMessage("$prefix §cDieser Spieler ist aktuell nicht bereit um eine Challenge zu spielen!")
            return false
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "nohit" -> {
                player.removeScoreboardTag("LS_2")
                player.addScoreboardTag("LS_1")
                sender.sendMessage("$prefix Der Spieler §9${player.name}§7 ist nun in Team 1!")
                if (sender != player) player.sendMessage("$prefix Du wurdest in Team 1 verschoben!")
            }
            "nosee" -> {
                player.removeScoreboardTag("LS_1")
                player.addScoreboardTag("LS_2")
                sender.sendMessage("$prefix Der Spieler §9${player.name}§7 ist nun in Team 2!")
                if (sender != player) player.sendMessage("$prefix Du wurdest in Team 2 verschoben!")
            }
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (args.size < 2) {
            list.add("noHit")
            list.add("noSee")
        } else if (args.size == 2) {
            onlinePlayers.forEach { if (!Spectator.isSpectator(it.uniqueId)) list.add(it.name) }
        }
        return list
    }
}