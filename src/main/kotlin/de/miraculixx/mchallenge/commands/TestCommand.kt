package de.miraculixx.mchallenge.commands

import de.miraculixx.kpaper.extensions.server
import de.miraculixx.kpaper.scoreboard.createCustomScoreboard
import de.miraculixx.mcommons.text.cmp
import dev.jorel.commandapi.kotlindsl.*
import io.papermc.paper.scoreboard.numbers.NumberFormat
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.ScoreboardManager
import java.util.UUID

class TestCommand {
    val command = commandTree("mtest") {
        withPermission("mchallenge.command.mtest")

        literalArgument("flat") {
            blockStateArgument("block") {
                playerExecutor { player, args ->
                    val block = args[0] as BlockData
                    val world = WorldCreator(UUID.randomUUID().toString())
                        .type(WorldType.FLAT)
                        .environment(World.Environment.NORMAL)
                        .generatorSettings("{\"layers\": [{\"block\": \"bedrock\", \"height\": 1}, {\"block\": \"${block.material.name.lowercase()}\", \"height\": 2}], \"biome\":\"the_void\"}")
                        .createWorld()
                    player.teleportAsync(world!!.spawnLocation)
                }
            }
        }

        literalArgument("experimental") {
            playerExecutor { player, _ ->
                val worldData = (server as CraftServer).handle.server.worldData
                worldData.dataConfiguration = worldData.dataConfiguration.expandFeatures(FeatureFlagSet.of(FeatureFlags.UPDATE_1_21))
                player.sendMessage("Experimental features enabled")
                player.inventory.addItem(ItemStack(Material.WIND_CHARGE), ItemStack(Material.MACE))
            }
        }

        literalArgument("scoreboard") {
            fun Objective.setScore(score: Int, display: Component) {
                val entry = getScore(score.toString()) // Get unique entry
                entry.customName(display) // Apply name
                entry.score = score // Apply score for ranking
                entry.numberFormat(NumberFormat.blank()) // Hide score number
            }

            playerExecutor { player, _ ->
                val myScoreboard = Bukkit.getScoreboardManager().newScoreboard
                player.scoreboard = myScoreboard
                val obj = myScoreboard.registerNewObjective("test", Criteria.DUMMY, cmp("Test Scoreboard"))
                obj.displaySlot = DisplaySlot.SIDEBAR

                // Display
                repeat(17) { id ->
                    obj.setScore(id, cmp("Score $id"))
                }
            }
        }
    }
}
