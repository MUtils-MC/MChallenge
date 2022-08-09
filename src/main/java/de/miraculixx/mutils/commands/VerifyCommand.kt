package de.miraculixx.mutils.commands

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.API
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.premium
import de.miraculixx.mutils.utils.tools.error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.net.InetAddress
import kotlin.random.Random

class VerifyCommand: CommandExecutor, TabCompleter {
    private var cooldown = false

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("$prefix §cSorry, only Players can verify MUtils to prevent automation of this process!")
            return false
        }
        if (args.isEmpty()) {
            sender.sendMessage(msg("command.verify.help"))
            return false
        }
        if (premium) {
            sender.sendMessage(msg("command.verify.verified"))
            sender.error()
            return false
        }
        if (cooldown) {
            sender.sendMessage(msg("command.cooldown"))
            sender.error()
            return false
        }

        val input = args[0]
        val server = Main.INSTANCE.server
        val ip = InetAddress.getLocalHost().hostAddress + ":" + server.port
        cooldown = true
        taskRunLater(20*5) {
            cooldown = false
        }

        CoroutineScope(Dispatchers.Default).launch {
            val uuid = sender.uniqueId.toString().replace("-","")
            sender.sendMessage("$prefix Connecting to Service...")
            if (!"[a-zA-Z\\d-]+".toRegex().matches(input)) {
                delay(Random.nextLong(200,400))
                sender.sendMessage("$prefix §cYour input contains illegal characters! Keys only contain A-Z a-z 0-9 -")
                sender.error()
                return@launch
            }
            val response = API.verify(input, ip, uuid)
            if (response == null || response.length < 2) {
                sender.sendMessage("$prefix §cAn Error occurred. Please take a look into your console for further information!")
                sender.error()
                return@launch
            }

            //Save verify data
            val config = ConfigManager.getConfig(Configs.VERIFY)
            config["Licence Key"] = input
            config["Licence Owner.MC"] = uuid
            ConfigManager.save(Configs.VERIFY)

            if (!response.startsWith("true")) {
                sender.sendMessage(msg("command.verify.failed", sender, input, response))
                sender.error()
                return@launch
            }

            val component = literalText(msg("command.verify.success", sender, response.split('-')[1].removePrefix(" "))) {
                onClickCommand("/restart")
            }
            sender.sendMessage(component)
            sender.playSound(sender, Sound.BLOCK_BEACON_ACTIVATE, 1f,1f)
            
            sender.location.toSimpleBlockString()
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        return if (args.size < 2) mutableListOf("<key>")
        else mutableListOf()
    }
}

fun Location.toSimpleBlockString() = "[$blockX, $blockY, $blockZ]"