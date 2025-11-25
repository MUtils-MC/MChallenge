package de.miraculixx.mchallenge.modules.mods.worldChanging.blockWorld

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
    private val materials = Material.entries.filter {
        it.isBlock && !it.isLegacy && isValidBlock(it)
    }

    private val overworld = WorldCreator.name(worldName).generator(ChunkProvider(materials)).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NORMAL).createWorld()
    private val nether = WorldCreator.name("${worldName}_nether").generator(ChunkProvider(materials)).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NETHER).createWorld()
    private val end = WorldCreator.name("${worldName}_the_end").generator(ChunkProvider(materials)).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.THE_END).createWorld()

    init {
        println(materials)
    }

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

    private fun isValidBlock(m: Material): Boolean {
        if (!m.isSolid) return false
        val name = m.name
        if (
            Tag.ALL_SIGNS.isTagged(m) || Tag.BANNERS.isTagged(m) || Tag.BANNERS.isTagged(m) ||
            Tag.BEDS.isTagged(m) || Tag.WOOL_CARPETS.isTagged(m) || Tag.SHULKER_BOXES.isTagged(m) ||
            Tag.STAIRS.isTagged(m) || Tag.SLABS.isTagged(m) || Tag.DOORS.isTagged(m) ||
            Tag.FLOWER_POTS.isTagged(m) || Tag.FLOWERS.isTagged(m) || Tag.LEAVES.isTagged(m) ||
            Tag.SAPLINGS.isTagged(m) || Tag.CORALS.isTagged(m) || Tag.WALLS.isTagged(m) ||
            Tag.FENCES.isTagged(m) || Tag.FENCE_GATES.isTagged(m) || Tag.WALLS.isTagged(m) ||
            Tag.FENCES.isTagged(m) || Tag.FENCE_GATES.isTagged(m) || Tag.BUTTONS.isTagged(m) ||
            Tag.TRAPDOORS.isTagged(m) || Tag.PRESSURE_PLATES.isTagged(m) || Tag.RAILS.isTagged(m) ||
            Tag.CLIMBABLE.isTagged(m) || Tag.CANDLES.isTagged(m) || Tag.CANDLE_CAKES.isTagged(m) ||
            Tag.ANVIL.isTagged(m)
        ) return false

        if (
            name.endsWith("GLASS") || name.endsWith("GLASS_PANE") || name.endsWith("HANGING_SIGN") ||
            name.endsWith("HEAD") || name.contains("CORAL") || name.endsWith("_GRATE") ||
            name.contains("CHERRY") || name.contains("BAMBOO") || name.startsWith("SUSPICIOUS") ||
            name.endsWith("_HANGING_SIGN") || name.contains("CAULDRON")
        ) return false


        when (m) {
            Material.KELP, Material.DRAGON_EGG, Material.SOUL_LANTERN,
            Material.LANTERN, Material.TORCH, Material.SOUL_TORCH,
            Material.TURTLE_EGG, Material.AMETHYST_CLUSTER, Material.SMALL_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD, Material.IRON_CHAIN, Material.COPPER_CHAIN,
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
            Material.SCULK_CATALYST, Material.SCULK_SENSOR, Material.SCULK_SHRIEKER,
            Material.LIGHTNING_ROD, Material.IRON_BARS, Material.DIRT_PATH, Material.VAULT,
            Material.MANGROVE_ROOTS, Material.SNIFFER_EGG, Material.CACTUS,
            Material.TORCHFLOWER, Material.PINK_PETALS, Material.CHISELED_BOOKSHELF,
            Material.DECORATED_POT, Material.TORCHFLOWER_SEEDS, Material.TRIAL_SPAWNER,
            Material.CALIBRATED_SCULK_SENSOR, Material.CRAFTER, Material.END_PORTAL_FRAME,
            Material.COMPOSTER, Material.STONECUTTER, Material.SLIME_BLOCK,
            Material.CAKE, Material.HONEY_BLOCK, Material.COPPER_LANTERN
                -> return false

            else -> return true
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