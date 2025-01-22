package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.utils.config.Configurable
import de.miraculixx.mchallenge.utils.config.loadConfig
import de.miraculixx.mchallenge.utils.config.saveConfig
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import dev.jorel.commandapi.IStringTooltip
import dev.jorel.commandapi.StringTooltip
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.*
import kotlinx.serialization.Serializable
import java.io.File

object PositionCommand : Configurable {
    private val file = File("${MChallenge.configFolder.path}/data/positions.json")
    private val positions: MutableMap<String, LiteLocation> = file.loadConfig(mutableMapOf())

    @Suppress("unused")
    val command = commandTree("position") {
        withAliases("pos", "location", "loc")
        literalArgument("get") {
            argument(StringArgument("name").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips { getPositionNames() })) {
                anyExecutor { sender, args ->
                    val name = args[0] as String
                    sender.sendMessage(prefix + sender.msg("command.position.get", listOf(name, positions[name]?.toString() ?: "Unknown")))
                }
            }
        }
        literalArgument("new") {
            stringArgument("name") {
                playerExecutor { player, args ->
                    val name = args[0] as String
                    val location = player.location
                    val liteLoc = LiteLocation(location.blockX, location.blockY, location.blockZ, location.world.name)
                    positions[name] = liteLoc
                    player.sendMessage(prefix + player.msg("command.position.new", listOf(name, liteLoc.toString())))
                }
            }
        }
        literalArgument("remove") {
            argument(StringArgument("name").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips { getPositionNames() })) {
                anyExecutor { sender, args ->
                    val name = args[0] as String
                    val position = positions.remove(name)
                    sender.sendMessage(prefix + sender.msg("command.position.remove", listOf(name, position?.toString() ?: "Unknown")))
                }
            }
        }
        literalArgument("reset") {
            anyExecutor { sender, _ ->
                positions.clear()
                sender.sendMessage(prefix + sender.msg("command.position.reset"))
            }
        }.withPermission("mutils.position.reset")
    }

    private fun getPositionNames(): Array<IStringTooltip> {
        return positions.map { StringTooltip.ofString(it.key, it.value.toString()) }.toTypedArray()
    }

    override fun save() {
        file.saveConfig(positions)
    }

    override fun reset() {
        positions.clear()
        save()
    }
}

@Serializable
data class LiteLocation(val x: Int, val y: Int, val z: Int, val world: String) {
    override fun toString() = "$x $y $z ($world)"
}