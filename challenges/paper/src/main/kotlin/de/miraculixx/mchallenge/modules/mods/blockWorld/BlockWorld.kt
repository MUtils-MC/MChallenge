package de.miraculixx.mchallenge.modules.mods.blockWorld

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.io.File
import java.util.*


class BlockWorld : Challenge {
    private val worldName = UUID.randomUUID().toString()
    private val materials = Material.values().filter {
        it.isBlock && !isTileEntity(it)
    }

    private val overworld = WorldCreator.name(worldName).generator(ChunkProvider(materials)).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NORMAL).createWorld()
    private val nether = WorldCreator.name("${worldName}_nether").generator(ChunkProvider(materials)).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NETHER).createWorld()
    private val end = WorldCreator.name("${worldName}_the_end").generator(ChunkProvider(materials)).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.THE_END).createWorld()

    override fun register() {
        onPortal.register()
    }

    override fun unregister() {
        onPortal.unregister()
    }

    override fun start(): Boolean {
        val loc = overworld?.spawnLocation ?: return false
        onlinePlayers.forEach { p -> p.teleportAsync(loc) }
        return true
    }

    override fun stop() {
        val loc = worlds[0].spawnLocation
        onlinePlayers.forEach { p -> p.teleportAsync(loc) }

        overworld.removeWorld()
        nether.removeWorld()
        end.removeWorld()
    }

    private fun World?.removeWorld() {
        this?.let { Bukkit.unloadWorld(it, false) }
        File(worldName).deleteRecursively()
    }

    private fun isTileEntity(material: Material): Boolean {
        if (!material.isSolid) return true
        if (Tag.ALL_SIGNS.isTagged(material)) return true
        if (Tag.BANNERS.isTagged(material)) return true
        if (Tag.BEDS.isTagged(material) || Tag.WOOL_CARPETS.isTagged(material)) return true
        if (Tag.SHULKER_BOXES.isTagged(material)) return true
        if (Tag.STAIRS.isTagged(material) || Tag.SLABS.isTagged(material) || Tag.DOORS.isTagged(material)) return true
        if (Tag.FLOWER_POTS.isTagged(material) || Tag.FLOWERS.isTagged(material) || Tag.LEAVES.isTagged(material) || Tag.SAPLINGS.isTagged(material) || Tag.CORALS.isTagged(material)) return true
        if (Tag.WALLS.isTagged(material) || Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material)) return true
        if (Tag.BUTTONS.isTagged(material) || Tag.TRAPDOORS.isTagged(material) || Tag.PRESSURE_PLATES.isTagged(material) || Tag.RAILS.isTagged(material)) return true
        if (Tag.CLIMBABLE.isTagged(material) || Tag.CANDLES.isTagged(material) || Tag.CANDLE_CAKES.isTagged(material)) return true
        val name = material.name
        if (name.endsWith("GLASS") || name.endsWith("GLASS_PANE")) return true
        if (name.endsWith("HANGING_SIGN") || name.endsWith("HEAD")) return true
        if (name.contains("CORAL")) return true


        return when (material) {
            Material.KELP, Material.DRAGON_EGG, Material.SOUL_LANTERN, Material.LANTERN, Material.TORCH, Material.SOUL_TORCH, Material.TURTLE_EGG, Material.AMETHYST_CLUSTER, Material.AMETHYST_SHARD, Material.BAMBOO, Material.END_ROD, Material.SEAGRASS, Material.TALL_SEAGRASS, Material.GRASS, Material.TALL_GRASS, Material.BEEHIVE, Material.LAVA, Material.WATER, Material.SNOW, Material.GRINDSTONE, Material.POINTED_DRIPSTONE, Material.BEE_NEST, Material.CHEST, Material.TRAPPED_CHEST, Material.DISPENSER, Material.FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.SHULKER_BOX, Material.BARREL, Material.SMOKER, Material.BLAST_FURNACE, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.LECTERN, Material.BEACON, Material.SPAWNER, Material.JUKEBOX, Material.ENCHANTING_TABLE, Material.END_PORTAL, Material.ENDER_CHEST, Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.SKELETON_WALL_SKULL, Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_WALL_SKULL, Material.CREEPER_HEAD, Material.DRAGON_HEAD, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK, Material.JIGSAW, Material.DAYLIGHT_DETECTOR, Material.COMPARATOR, Material.CONDUIT, Material.BELL, Material.END_GATEWAY, Material.SCULK_CATALYST, Material.SCULK_SENSOR, Material.SCULK_SHRIEKER -> true

            else -> false
        }
    }

    private val onPortal = listen<PlayerTeleportEvent>(register = false) {
        val fromWorld = it.from.world
        val toWorld = it.to.world
        if (fromWorld == toWorld) return@listen
        when (toWorld.environment) {
            World.Environment.NETHER -> it.to.world = nether
            World.Environment.NORMAL -> it.to.world = overworld
            World.Environment.THE_END -> it.to.world = end
            else -> Unit
        }
    }

    private class ChunkProvider(private val materials: List<Material>) : ChunkGenerator() {
        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            val material = materials.random()
            (0..15).forEach { x ->
                (0..15).forEach { z ->
                    (worldInfo.minHeight until worldInfo.maxHeight).forEach { y ->
                        val type = chunkData.getType(x, y, z)
                        if (!type.isAir && type != Material.BEDROCK && type != Material.WATER && type != Material.LAVA && type != Material.END_PORTAL_FRAME) chunkData.setBlock(x, y, z, material)
                    }
                }
            }
        }

        override fun createVanillaChunkData(world: World, x: Int, z: Int): ChunkData {
            return super.createVanillaChunkData(world, x, z)
        }

        override fun shouldGenerateNoise(): Boolean {
            return true
        }

        override fun shouldGenerateSurface(): Boolean {
            return true
        }

        @Deprecated("Deprecated in Java", ReplaceWith("true"))
        override fun shouldGenerateBedrock(): Boolean {
            return true
        }

        override fun shouldGenerateCaves(): Boolean {
            return true
        }

        override fun shouldGenerateDecorations(): Boolean {
            return true
        }

        override fun shouldGenerateMobs(): Boolean {
            return true
        }

        override fun shouldGenerateStructures(): Boolean {
            return true
        }
    }
}