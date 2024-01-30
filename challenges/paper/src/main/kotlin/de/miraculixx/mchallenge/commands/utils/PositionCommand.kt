package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.json
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import dev.jorel.commandapi.IStringTooltip
import dev.jorel.commandapi.StringTooltip
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File

class PositionCommand {
    private val positions: MutableMap<String, LiteLocation>
    private val file = File("${MChallenge.configFolder.path}/data/positions.json")

    init {
        positions = json.decodeFromString(file.readJsonString(true))
    }

    val command = commandTree("position") {
        withAliases("pos", "location", "loc")
        literalArgument("get") {
            argument(StringArgument("name").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips { getPositionNames() })) {
                anyExecutor { commandSender, args ->
                    val name = args[0] as String
                    commandSender.sendMessage(prefix + msg("command.position.get", listOf(name, positions[name]?.toString() ?: "Unknown")))
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
                    player.sendMessage(prefix + msg("command.position.new", listOf(name, liteLoc.toString())))
                }
            }
        }
        literalArgument("remove") {
            argument(StringArgument("name").replaceSuggestions(ArgumentSuggestions.stringsWithTooltips { getPositionNames() })) {
                anyExecutor { commandSender, args ->
                    val name = args[0] as String
                    val position = positions.remove(name)
                    commandSender.sendMessage(prefix + msg("command.position.remove", listOf(name, position?.toString() ?: "Unknown")))
                }
            }
        }
        literalArgument("reset") {
            anyExecutor { commandSender, _ ->
                positions.clear()
                commandSender.sendMessage(prefix + msg("command.position.reset"))
            }
        }.withPermission("mutils.position.reset")
    }

    private fun getPositionNames(): Array<IStringTooltip> {
        return positions.map { StringTooltip.ofString(it.key, it.value.toString()) }.toTypedArray()
    }

    fun saveFile() {
        file.writeText(json.encodeToString(positions))
    }

    fun reset() {
        positions.clear()
    }
}

@Serializable
data class LiteLocation(val x: Int, val y: Int, val z: Int, val world: String) {
    override fun toString() = "$x $y $z ($world)"
}