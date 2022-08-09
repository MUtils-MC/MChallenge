package de.miraculixx.mutils.modules.worldManager

import de.miraculixx.mutils.enums.modules.worldCreator.BiomeProviders
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import net.axay.kspigot.chat.sendText
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class WorldCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() && sender is Player) {
            GUIBuilder(sender, GUI.WORLD_MAIN, GUIAnimation.WATERFALL_OPEN).custom().open()
            return true
        }
        if (!(args.size == 1 && args[0] == "info") && args.size < 2) {
            sender.sendMessage(msg("command.world.help", pre = false))
            return false
        }
        // world create <name> <Environment> <BiomeProvider> <Seed>

        val manager = WorldTools()
        when (args[0]) {
            "create" -> {
                if (args.size < 3) {
                    sender.sendMessage(msg("command.world.help", pre = false))
                    return false
                }
                val providerS = if (args.size < 4) null else args[3]
                val seedS = if (args.size < 5) null else args[4]
                if (!initiateWorldCreate(manager, sender, args[1], args[2], providerS, seedS)) {
                    sender.sendMessage(msg("command.world.error"))
                    return false
                }
            }

            "tp" -> {
                val name = args[1]
                val world = Bukkit.getWorld(name)
                if (world == null) {
                    sender.sendMessage("$prefix §cDie Welt $name wurde nicht gefunden!")
                    return false
                } else (sender as Player).teleport(world.getHighestBlockAt(0, 0).location)
            }

            "delete" -> {
                val name = args[1]
                val world = Bukkit.getWorld(name)
                if (manager.deleteWorld(world)) sender.sendMessage("$prefix §aDie Welt $name wurde erfolgreich gelöscht!")
                else sender.sendMessage("$prefix §cDie Welt $name konnte nicht gelöscht werden! §7(Existiert nicht?)")
            }
            "info" -> {
                if (sender !is Player) {
                    sender.sendMessage(msg("command.notPlayer"))
                    return false
                }
                val world = sender.world
                sender.sendMessage(
                    "§9§m        §9[ §fWorld Info §9]§9§m        \n" +
                            "§7Name ≫ §9${world.name}\n" +
                            "§7Environment ≫ §9${world.environment.name}\n" +
                            "§7Current Biome ≫ §9${world.getBiome(sender.location)}"
                )
            }
        }
        return true
    }

    private fun initiateWorldCreate(manager: WorldTools, sender: CommandSender,
                                    nameS: String, typeS: String, providerS: String?, seedS: String?): Boolean {
        val type = if (typeS.uppercase() == "ALL") {
            initiateWorldCreate(manager, sender, nameS, "NETHER", providerS, seedS)
            initiateWorldCreate(manager, sender, nameS, "THE_END", providerS, seedS)
            World.Environment.NORMAL
        } else try {
            World.Environment.valueOf(typeS)
        } catch (_: IllegalArgumentException) {
            return false
        }

        val env = when (type) {
            World.Environment.NORMAL -> "O"
            World.Environment.NETHER -> "N"
            World.Environment.THE_END -> "E"
            else -> "C"
        }

        val provider = if (providerS != null) try {
            BiomeProviders.valueOf(providerS)
        } catch (_: IllegalArgumentException) {
            BiomeProviders.VANILLA
        }
        else BiomeProviders.VANILLA

        val seed = seedS?.toLongOrNull(36)

        val name = "$nameS-$env".replace("#", "")

        val world = manager.createWorld(name, type, provider, seed)
        return if (world != null) {
            sender.sendText {
                text("$prefix §aNeue Welt wurde erstellt! §7(click to teleport)\n"+
                        "$prefix §7Name: §9$name\n" +
                        "$prefix §7Seed: §9${seed?:"§oRandom"}\n" +
                        "$prefix §7Environment: §9${type.name}\n" +
                        "$prefix §7Generator: §9${provider.name}")
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world tp $name")
                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§7Click to teleport"))
            }
            true
        } else false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        when (args.size){
            0,1 -> {
                list.add("create")
                list.add("tp")
                list.add("delete")
                list.add("info")
            }

            2 -> {
                when {
                    args[0] == "create" && args[1].startsWith("#") -> Biome.values().forEach { list.add(it.name) }
                    args[0] == "create" -> {
                        list.add("<name>")
                        list.add("#")
                    }
                    args[0] == "delete" || args[0] == "tp" -> Bukkit.getWorlds().forEach { list.add(it.name) }
                }
            }

            else -> {
                if (args[0] == "create") {
                    when (args.size) {
                        3 -> {
                            World.Environment.values().forEach { list.add(it.name) }
                            list.remove("CUSTOM")
                            list.add("ALL")
                        }
                        4 -> BiomeProviders.values().forEach { list.add(it.name) }
                        5 -> list.add("<seed>")
                    }
                }
            }
        }
        return list
    }
}