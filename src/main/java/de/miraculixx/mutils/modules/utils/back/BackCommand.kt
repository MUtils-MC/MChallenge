package de.miraculixx.mutils.modules.utils.back

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.msg
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BackCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.noPlayer"))
            return false
        }

        val cBack = ConfigManager.getConfig(Configs.BACK)
        val uuid = sender.uniqueId.toString()
        val currentLoc = sender.location
        if (!cBack.isSet("$uuid.Location") || !cBack.isSet("$uuid.World")) {
            sender.sendMessage(msg("command.back.noLocation"))
            return false
        }
        val cords = cBack.getString("$uuid.Location")?.split(" ")
        val world = Bukkit.getWorld(cBack.getString("$uuid.World")!!)
        if (cords == null || cords.size != 3 || world == null) {
            sender.sendMessage(msg("command.back.noLocation"))
            return false
        }

        val loc = Location(
            world,
            cords[0].replace(" ", "").toDouble(),
            cords[1].replace(" ", "").toDouble(),
            cords[2].replace(" ", "").toDouble(),
            currentLoc.yaw,
            currentLoc.pitch
        )

        sender.teleport(loc)
        sender.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        sender.sendMessage(msg("command.back.teleport"))
        return true
    }
}