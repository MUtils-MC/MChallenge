package de.miraculixx.mchallenge.commands

import de.miraculixx.kpaper.extensions.server
import dev.jorel.commandapi.kotlindsl.*
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.inventory.ItemStack
import java.util.UUID

class TestCommand {
    val command = commandTree("test") {
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

        literalArgument("experimantal") {
            playerExecutor { player, _ ->
                val worldData = (server as CraftServer).handle.server.worldData
                worldData.dataConfiguration = worldData.dataConfiguration.expandFeatures(FeatureFlagSet.of(FeatureFlags.UPDATE_1_21))
                player.sendMessage("Experimental features enabled")
                player.inventory.addItem(ItemStack(Material.WIND_CHARGE), ItemStack(Material.MACE))
            }
        }
    }
}