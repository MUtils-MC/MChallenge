package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.speedrun.PrepareWorld
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleMessage
import net.axay.kspigot.extensions.bukkit.feed
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.advancement.Advancement
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ResetCommand : CommandExecutor, TabCompleter {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        ModuleManager.setTimerStatus(false)
        ModuleManager.setTime(0,0,0,0)

        if (ModuleManager.isActive(Modules.SPEEDRUN)) {
            onlinePlayers.forEach {
                it.inventory.clear()
                it.heal()
                it.feed()
                it.exp = 0f
                it.level = 0
                it.fireTicks = 0
                it.gameMode = GameMode.SURVIVAL
                it.enderChest.clear()
                removeAll(it)
            }
            val manager = PrepareWorld()
            worlds.forEach {
                if (it.name.contains("speedrun"))
                    manager.deleteWorld(it)
            }
            manager.newWorld(ConfigManager.getConfig(Configs.SPEEDRUN))
            return true
        }

        onlinePlayers.forEach {
            it.kickPlayer(
                "§1>§m             §1[ §6§lRESET§1 ]§1§m             §1<" +
                        "\n\n§9World reset by §l${p0.name}\n\n" +
                        "§1>§m             §1[ §6§lRESET§1 ]§1§m             §1<"
            )
        }

        consoleMessage("$prefix Delete old Worlds...")

        val config = ConfigManager.getConfig(Configs.SETTINGS)
        config.set("Legacy Reset", true)
        val worldList = worlds.map { it.name }
        config.set("Loaded Worlds", worldList)

        Bukkit.shutdown()
        return true

    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        return ArrayList()
    }

    private fun removeAll(player: Player) {
        val iterator: Iterator<Advancement> = Bukkit.getServer().advancementIterator()
        while (iterator.hasNext()) {
            val progress: AdvancementProgress = player.getAdvancementProgress(iterator.next())
            for (criteria in progress.awardedCriteria) progress.revokeCriteria(criteria)
        }
    }
}