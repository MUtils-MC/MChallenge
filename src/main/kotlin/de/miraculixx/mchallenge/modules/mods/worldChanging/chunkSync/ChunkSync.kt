package de.miraculixx.mchallenge.modules.mods.worldChanging.chunkSync

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import net.minecraft.core.Vec3i
import org.bukkit.*
import org.bukkit.block.Container
import org.bukkit.block.data.BlockData
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.Inventory

class ChunkSync : Challenge {
    private val changes: MutableMap<Vec3i, BlockData> = mutableMapOf()
    private val containerBlocks: MutableMap<Vec3i, Inventory> = mutableMapOf()

    private val envChanges: Boolean

    init {
        val settings = challenges.getSetting(Challenges.CHUNK_SYNC).settings
        envChanges = settings["env"]?.toBool()?.getValue() ?: false
    }

    override fun register() {
        onChunkLoad.register()
        onBlockPlace.register()
        onBlockBreak.register()
        onInvBlockClick.register()
        if (envChanges) {
            onBlockExplode.register()
            onEntityExplode.register()
        }
    }

    override fun unregister() {
        onChunkLoad.unregister()
        onBlockPlace.unregister()
        onBlockBreak.unregister()
        onBlockExplode.unregister()
        onEntityExplode.unregister()
        onInvBlockClick.unregister()
    }

    private val onChunkLoad = listen<ChunkLoadEvent>(register = false) {
        val chunk = it.chunk
        changes.forEach { (relative, change) ->
            chunk.placeBlock(relative, change)
        }
    }

    private val onBlockPlace = listen<BlockPlaceEvent>(register = false) {
        val block = it.blockPlaced
        val blockData = block.blockData
        updateChanges(blockData, block.location, block.chunk)
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        val block = it.block
        val relative = getRelativeCoords(block.location)
        containerBlocks.remove(relative)
        val blockData = Bukkit.createBlockData(Material.AIR)
        updateChanges(blockData, block.location, block.chunk)
    }

    private val onBlockExplode = listen<BlockExplodeEvent>(register = false) {
        val blockData = Bukkit.createBlockData(Material.AIR)
        updateChanges(it.blockList().map { block -> MultiChange(blockData, block.location, block.chunk) }.toSet(), it.block.world)
    }

    private val onEntityExplode = listen<EntityExplodeEvent>(register = false) {
        val blockData = Bukkit.createBlockData(Material.AIR)
        updateChanges(it.blockList().map { block -> MultiChange(blockData, block.location, block.chunk) }.toSet(), it.entity.world)
    }

    private val onInvBlockClick = listen<PlayerInteractEvent>(register = false) {
        if (it.action != Action.RIGHT_CLICK_BLOCK) return@listen
        val block = it.clickedBlock ?: return@listen
        val blockState = block.state
        if (blockState !is Container) return@listen
        val relative = getRelativeCoords(block.location)
        it.isCancelled = true
        val existingInv = containerBlocks[relative]
        if (existingInv == null) {
            val sourceInv = blockState.inventory
            containerBlocks[relative] = sourceInv
            it.player.openInventory(sourceInv)

        } else {
            it.player.openInventory(existingInv)
        }
    }

    //
    // Update Utilities
    //

    private fun Chunk.placeBlock(relative: Vec3i, blockData: BlockData) {
        val realX = (x * 16) + relative.x
        val realZ = (z * 16) + relative.z
        val block = world.getBlockAt(realX, relative.y, realZ)
        val type = block.type
        if (type == Material.END_PORTAL_FRAME || type == Material.NETHER_PORTAL || type == Material.END_PORTAL) return
        block.setBlockData(blockData, false)
    }

    private fun getRelativeCoords(location: Location): Vec3i {
        val chunk = location.chunk
        val rX = location.x - (chunk.x * 16)
        val rZ = location.z - (chunk.z * 16)
        return Vec3i(rX.toInt(), location.y.toInt(), rZ.toInt())
    }

    private fun updateChanges(blockData: BlockData, location: Location, currentChunk: Chunk) {
//        val currentTime = Instant.now().toEpochMilli()
        val relative = getRelativeCoords(location)
        changes[relative] = blockData

        location.world.loadedChunks.forEach { loaded ->
            if (loaded == currentChunk) return@forEach
            loaded.placeBlock(relative, blockData)
        }
//        val timeAfter = Instant.now().toEpochMilli()
//        val duration = (timeAfter - currentTime).milliseconds
    }

    private fun updateChanges(changeSet: Set<MultiChange>, world: World) {
        changeSet.forEach {
            it.relative = getRelativeCoords(it.location)
            changes[it.relative!!] = it.blockData
        }

        world.loadedChunks.forEach { loaded ->
            changeSet.forEach changeLoop@{
                if (loaded == it.currentChunk) return@changeLoop
                loaded.placeBlock(it.relative ?: return@changeLoop, it.blockData)
            }
        }
    }

    private data class MultiChange(val blockData: BlockData, val location: Location, val currentChunk: Chunk, var relative: Vec3i? = null)
}