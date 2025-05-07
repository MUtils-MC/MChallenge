package de.miraculixx.mchallenge.modules.mods.worldChanging.mineField

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.util.*

class MineFieldWorld : Challenge {
    private val worldName = UUID.randomUUID().toString()
    private lateinit var overworld: World
    private lateinit var nether: World
    private lateinit var end: World
    private var density: Int = 50

    init {
        val settings = challenges.getSetting(Challenges.MIRROR).settings
        density = settings["density"]?.toInt()?.getValue() ?: 50
    }

    override fun register() {
        onPortal.register()
        onPressurePlateStep.register()
        onBlockBreak.register()
    }

    override fun unregister() {
        worldInitEvent.unregister()
        onPortal.unregister()
        onPressurePlateStep.unregister()
        onBlockBreak.unregister()
    }

    override fun start(): Boolean {
        overworld = WorldCreator.name(worldName).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NORMAL).createWorld() ?: return false
        nether = WorldCreator.name("${worldName}_nether").keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NETHER).createWorld() ?: return false
        end = WorldCreator.name("${worldName}_end").keepSpawnLoaded(TriState.FALSE).environment(World.Environment.THE_END).createWorld() ?: return false

        val spawnBlock = overworld.getHighestBlockAt(0, 0)
        if (spawnBlock.type == Material.WATER || spawnBlock.type == Material.LAVA) {
            (spawnBlock.x - 1 .. spawnBlock.x + 1).forEach { x ->
                (spawnBlock.z - 1 .. spawnBlock.z + 1).forEach { z ->
                    overworld.getBlockAt(x, spawnBlock.y, z).type = Material.BEDROCK
                }
            }
        }

        val spawnLoc = spawnBlock.location.add(0.0, 2.0, 0.0)
        onlinePlayers.forEach { p -> p.teleportAsync(spawnLoc) }
        return true
    }

    override fun stop() {
        val loc = worlds[0].spawnLocation
        onlinePlayers.forEach { p -> p.teleportAsync(loc) }

        overworld.removeWorld()
        nether.removeWorld()
        end.removeWorld()

        onPhysics.unregister()
    }

    private fun World?.removeWorld() {
        this?.let { Bukkit.unloadWorld(it, false) }
        File(worldName).deleteRecursively()
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

    private val worldInitEvent = listen<WorldInitEvent> {
        it.world.populators.add(CustomBlockPopulator(density))
    }

    private val onPressurePlateStep = listen<PlayerInteractEvent>(register = false) {
        if (it.action != Action.PHYSICAL) return@listen
        val block = it.clickedBlock ?: return@listen
        if (block.type != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return@listen
        block.setType(Material.AIR, false)

        it.player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10, 1, false, false, false))
        it.player.setRespawnLocation(block.world.spawnLocation, true)
        block.world.createExplosion(block.location, 4.0f, false, false)
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        val block = it.block
        if (block.type == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            it.isCancelled = true
            block.setType(Material.AIR, false)
            block.world.createExplosion(block.location, 4.0f, false, false)
        }
    }

    private val onPhysics = listen<BlockPhysicsEvent>(register = false) {
        if (it.block.type == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            it.isCancelled = true
        }
    }


    private class CustomBlockPopulator(private val density: Int) : BlockPopulator() {
        private val pressurePlate = Bukkit.createBlockData(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)

        override fun populate(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, limitedRegion: LimitedRegion) {
            if (chunkX == 0 && chunkZ == 0) return // Skip spawn chunks

            val centerX = limitedRegion.centerBlockX
            val centerZ = limitedRegion.centerBlockZ
            (centerX - 8..centerX + 8).forEach x@{ x ->
                (centerZ - 8..centerZ + 8).forEach z@{ z ->
                    (worldInfo.minHeight + 1 until worldInfo.maxHeight - 1).forEach h@{ y ->
                        if (!limitedRegion.isInRegion(x, y, z)) return@h
                        val currentBlock = limitedRegion.getBlockState(x, y, z).type

                        // Skip if the block is air, not solid or a pressure plate
                        if (currentBlock.isAir || !currentBlock.isSolid || currentBlock == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return@h

                        val topBlock = limitedRegion.getBlockState(x, y + 1, z).type
                        if (!topBlock.isAir && !Tag.REPLACEABLE.isTagged(topBlock) && topBlock != Material.SNOW) return@h

                        if ((0..100).random() < density) {
                            limitedRegion.setBlockData(x, y + 1, z, pressurePlate)
                        }
                    }
                }
            }
        }
    }
}