package de.miraculixx.mchallenge.modules.mods.worldChanging.mineField

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.block.Action
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
    }

    override fun unregister() {
        worldInitEvent.unregister()
        onPortal.unregister()
        onPressurePlateStep.unregister()
    }

    override fun start(): Boolean {
        overworld = WorldCreator.name(worldName).keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NORMAL).createWorld() ?: return false
        nether = WorldCreator.name("${worldName}_nether").keepSpawnLoaded(TriState.FALSE).environment(World.Environment.NETHER).createWorld() ?: return false
        end = WorldCreator.name("${worldName}_end").keepSpawnLoaded(TriState.FALSE).environment(World.Environment.THE_END).createWorld() ?: return false

        val loc = overworld.spawnLocation
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
        it.player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 2, false, false, false))
        val tnt = block.world.spawnEntity(block.location.add(.5,.0,.5), EntityType.PRIMED_TNT) as TNTPrimed
        tnt.fuseTicks = 1
        tnt.setIsIncendiary(false)
    }

    private data class Position(val x: Int, val y: Int, val z: Int)

    private class CustomBlockPopulator(private val density: Int) : BlockPopulator() {
        private val pressurePlate = Bukkit.createBlockData(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
        private val a = Bukkit.createBlockData(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
        private val progressedBlocks: MutableSet<Position> = mutableSetOf()

        override fun populate(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, limitedRegion: LimitedRegion) {
            val buffer = limitedRegion.buffer
            val direction = buffer + (buffer / 2)
            val centerX = limitedRegion.centerBlockX
            val centerZ = limitedRegion.centerBlockZ
            (centerX - direction..centerX + direction).forEach x@{ x ->
                (centerZ - direction..centerZ + direction).forEach z@{ z ->
                    (worldInfo.minHeight + 1 until worldInfo.maxHeight - 1).forEach h@{ y ->
                        if (!limitedRegion.isInRegion(x, y, z)) return@h
                        val currentBlock = limitedRegion.getBlockState(x, y, z).type
                        if (currentBlock.isAir || !currentBlock.isSolid || currentBlock == Material.HEAVY_WEIGHTED_PRESSURE_PLATE || currentBlock == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return@h
                        val topBlock = limitedRegion.getBlockState(x, y + 1, z).type
                        val pos = Position(x, y + 1, z)
                        if ((!topBlock.isAir && !Tag.REPLACEABLE.isTagged(topBlock) && topBlock != Material.SNOW) || progressedBlocks.contains(pos)) return@h

                        progressedBlocks.add(pos)
                        if ((0..100).random() <= density) {
                            limitedRegion.setBlockData(x, y + 1, z, pressurePlate)
                        }
                    }
                }
            }
        }
    }
}