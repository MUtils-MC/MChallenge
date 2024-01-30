package de.miraculixx.mchallenge.modules.mods.worldChanging.blockWorld

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mvanilla.messages.majorVersion
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.io.File
import java.util.*


class BlockWorld : Challenge {
    private val worldName = UUID.randomUUID().toString()
    private val materials = Material.entries.filter {
        it.isBlock && !isInvalidItem(it)
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
        overworld?.setGameRule(GameRule.DO_FIRE_TICK, false)
        nether?.setGameRule(GameRule.DO_FIRE_TICK, false)
        end?.setGameRule(GameRule.DO_FIRE_TICK, false)
        val loc = overworld?.spawnLocation ?: throw IllegalArgumentException("World could not be loaded! Do you use an unsupported version?")
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

    private fun isInvalidItem(material: Material): Boolean {
        if (!material.isSolid) return true
        val name = material.name
        if (
            Tag.ALL_SIGNS.isTagged(material) || Tag.BANNERS.isTagged(material) || Tag.BANNERS.isTagged(material) ||
            Tag.BEDS.isTagged(material) || Tag.WOOL_CARPETS.isTagged(material) || Tag.SHULKER_BOXES.isTagged(material) ||
            Tag.STAIRS.isTagged(material) || Tag.SLABS.isTagged(material) || Tag.DOORS.isTagged(material) ||
            Tag.FLOWER_POTS.isTagged(material) || Tag.FLOWERS.isTagged(material) || Tag.LEAVES.isTagged(material) ||
            Tag.SAPLINGS.isTagged(material) || Tag.CORALS.isTagged(material) || Tag.WALLS.isTagged(material) ||
            Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material) || Tag.WALLS.isTagged(material) ||
            Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material) || Tag.BUTTONS.isTagged(material) ||
            Tag.TRAPDOORS.isTagged(material) || Tag.PRESSURE_PLATES.isTagged(material) || Tag.RAILS.isTagged(material) ||
            Tag.CLIMBABLE.isTagged(material) || Tag.CANDLES.isTagged(material) || Tag.CANDLE_CAKES.isTagged(material) ||
            Tag.ANVIL.isTagged(material)
        ) return true

        if (
            name.endsWith("GLASS") || name.endsWith("GLASS_PANE") || name.endsWith("HANGING_SIGN") ||
            name.endsWith("HEAD") || name.contains("CORAL") || name.startsWith("SUSPICIOUS")
        ) return true

        // Experimental 1.21
        if (name.contains("COPPER") && (name.contains("DOOR") || name.contains("GRATE"))) return true

        if (majorVersion < 20) {
            if (
                name.contains("CHERRY") || name.contains("BAMBOO") ||
                name.endsWith("_HANGING_SIGN")
            ) return true
            when (name) {
                "TORCHFLOWER", "PINK_PETALS", "CHISELED_BOOKSHELF",
                "DECORATED_POT", "TORCHFLOWER_SEEDS", "GRASS" -> return true
            }
        } else {
            // rename GRASS -> SHORT_GRASS
            if (name == "SHORT_GRASS")return true
        }

        return when (material) {
            Material.KELP, Material.DRAGON_EGG, Material.SOUL_LANTERN,
            Material.LANTERN, Material.TORCH, Material.SOUL_TORCH,
            Material.TURTLE_EGG, Material.AMETHYST_CLUSTER, Material.SMALL_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD, Material.CHAIN,
            Material.BAMBOO, Material.END_ROD, Material.SEAGRASS,
            Material.TALL_SEAGRASS, Material.SHORT_GRASS, Material.TALL_GRASS,
            Material.BEEHIVE, Material.LAVA, Material.WATER,
            Material.SNOW, Material.GRINDSTONE, Material.POINTED_DRIPSTONE,
            Material.BEE_NEST, Material.CHEST, Material.TRAPPED_CHEST,
            Material.DISPENSER, Material.FURNACE, Material.BREWING_STAND,
            Material.HOPPER, Material.DROPPER, Material.SHULKER_BOX,
            Material.BARREL, Material.SMOKER, Material.BLAST_FURNACE,
            Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.LECTERN,
            Material.BEACON, Material.SPAWNER, Material.JUKEBOX,
            Material.ENCHANTING_TABLE, Material.END_PORTAL, Material.ENDER_CHEST,
            Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
            Material.SKELETON_WALL_SKULL, Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_WALL_SKULL,
            Material.CREEPER_HEAD, Material.DRAGON_HEAD, Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK,
            Material.JIGSAW, Material.DAYLIGHT_DETECTOR, Material.COMPARATOR,
            Material.CONDUIT, Material.BELL, Material.END_GATEWAY,
            Material.SCULK_CATALYST, Material.CALIBRATED_SCULK_SENSOR, Material.SCULK_SENSOR, Material.SCULK_SHRIEKER,
            Material.LIGHTNING_ROD, Material.IRON_BARS, Material.DIRT_PATH, -> true

            else -> false
        }
    }

    @Suppress("DuplicatedCode")
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
        private val chunkMaterial: MutableMap<String, Material> = mutableMapOf()

        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            val material = materials.random()
            chunkMaterial["${chunkX}_$chunkZ"] = material
            (0..15).forEach { x ->
                (0..15).forEach { z ->
                    (worldInfo.minHeight + 1 until worldInfo.maxHeight).forEach { y ->
                        val type = chunkData.getType(x, y, z)
                        if (!type.isAir && type != Material.BEDROCK && type != Material.WATER && type != Material.LAVA && type != Material.END_PORTAL_FRAME) chunkData.setBlock(x, y, z, material)
                    }
                }
            }
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