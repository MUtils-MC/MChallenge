package de.miraculixx.mutils.commands

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.modules.utils.tools.Debugger
import de.miraculixx.mutils.utils.prefix
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.math.BigInteger
import java.util.*

class DebugChallenge : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {

        if (!sender.isOp || sender !is Player) {
            sender.sendMessage("$prefix Du musst ein Spieler sein!")
            return false
        }
        if (p3.isEmpty()) {
            sender.sendMessage("$prefix Bitte gebe einen gültigen Subcommand an!")
            return false
        }

        when (p3[0].lowercase(Locale.getDefault())) {
            "levi" -> {
                val block = sender.location.clone().add(0.5, -1.0, 0.5).block
                val fallingBlock = block.world.spawnFallingBlock(block.location, block.blockData)
                fallingBlock.dropItem = false
                fallingBlock.setGravity(false)
                fallingBlock.isInvulnerable = true
                fallingBlock.velocity = Vector(0.0, 0.1, 0.0)
                block.type = Material.AIR
            }
            "zahl" -> {
                var temp = BigInteger.ZERO
                while (true) {
                    temp = temp.add(BigInteger.ONE)
                    broadcast("§7Zahl: $temp")
                }
            }
            "show" -> {
                broadcast("$prefix Alle Spieler sind nun Sichtbar!")
                onlinePlayers.forEach { first ->
                    val player = first.player
                    onlinePlayers.forEach { second ->
                        val target = second.player
                        if (target != player) player!!.showPlayer(Main.INSTANCE, target!!)
                    }
                }
            }
            "debug" -> {
                if (sender.scoreboardTags.contains("DEBUG")) {
                    sender.removeScoreboardTag("DEBUG")
                    sender.sendMessage("$prefix §cOff")
                } else {
                    sender.addScoreboardTag("DEBUG")
                    sender.sendMessage("$prefix §aOn")
                    Debugger(sender)
                }
            }
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        list.add("dimSwap")
        list.add("levi")
        list.add("show")
        list.add("debug")

        return list
    }
}