package de.miraculixx.mutils.module

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mutils.MWorlds
import de.miraculixx.mutils.messages.cError
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.debug
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.io.File
import java.util.*

object WorldDataHandling {
    private val saveFolder = File("${MWorlds.configFolder.path}/playerdata")

    /**
     * [UUID] player uuid
     *
     * [PlayerData] owning player data's loaded in ram
     */
    private val playerData: MutableMap<UUID, PlayerData> = mutableMapOf()

    /**
     * [String] category name
     *
     * [List] list of worlds inside this category
     */
    private val categories: MutableMap<String, MutableList<World>> = mutableMapOf()

    /**
     * Prevent water/lava from flowing around on chunk generation. This will increase performance server and client side!
     */
    private val onWaterFlow = listen<BlockFromToEvent> {
        val block = it.toBlock
        if (!WorldManager.getLoadedWorlds().containsKey(block.world.uid)) return@listen
        if (block.chunk.inhabitedTime < 20) it.isCancelled = true
    }

    /**
     * Prevent sea-grass growing on near air positions to prevent block update mania (lag)
     */
    private val onGrow = listen<BlockGrowEvent> {
        val block = it.newState
        val world = block.world
        if (!WorldManager.getLoadedWorlds().containsKey(world.uid)) return@listen
        val sourceLoc = block.location
        for (x in -1..1) {
            for (z in -1..1) {
                if (world.getBlockAt(sourceLoc.blockX + x, sourceLoc.blockY, sourceLoc.blockZ + z).type == Material.AIR) {
                    it.isCancelled = true
                    return@listen
                }
            }
        }
    }

    /**
     * Redirect non bed/anchor respawns to the correct world.
     *
     * 1. Searches any Overworld
     *      -> Choose first
     * 2. Choose fist world (any env)
     * 3. Vanilla
     */
    private val onRespawn = listen<PlayerRespawnEvent> {
        if (it.isAnchorSpawn || it.isBedSpawn) return@listen
        val deathWorld = it.player.world
        val category = WorldManager.getWorldData(deathWorld.uid)?.category ?: "Vanilla"
        val worldList = categories[category]

        val respawnWorld = worldList?.firstOrNull { world ->
            world.environment == World.Environment.NORMAL
        } ?: worldList?.firstOrNull() ?: return@listen

        it.respawnLocation = respawnWorld.spawnLocation
        if (debug) it.player.sendMessage(cmp("Respawn in ${respawnWorld.name} - Death in ${deathWorld.name}"))
    }

    /**
     * Load category player data on joining
     */
    private val onJoin = listen<PlayerJoinEvent> {
        val player = it.player
        val newLoc = loadPlayerData(player, WorldManager.getWorldData(player.world.uid)?.category ?: "Vanilla") ?: return@listen
        player.teleport(newLoc)
    }

    /**
     * Save category player data on leaving
     */
    private val onLeave = listen<PlayerQuitEvent> {
        val player = it.player
        savePlayerData(player, WorldManager.getWorldData(player.world.uid)?.category ?: "Vanilla")
    }

    /**
     * Redirect to correct world on portal usage.
     *
     * Always the first world with matching category and dimension is used as destination. If no world was found, the portal act like disabled
     */
    private val onDimensionSwitch = listen<PlayerPortalEvent>(priority = EventPriority.HIGHEST) {
        val player = it.player
        val worldFrom = it.from.world
        val worldTo = it.to.world
        val category = WorldManager.getWorldData(worldFrom.uid)?.category ?: "Vanilla"
        val toDimension = worldTo.environment
        val correctWorld = categories[category]?.firstOrNull { world -> world.environment == toDimension }
        if (correctWorld == null) {
            it.isCancelled = true
            if (debug) player.sendMessage(cmp("No destination world found in category $category", cError))
            return@listen
        }
        it.to.world = correctWorld

        if (debug) player.sendMessage(cmp("World Swap: ${worldFrom.name} -> ${it.to.world.name}"))
    }

    /**
     * Load player data on world switch. If no data is found, the player is handled like on first join
     */
    private val onWorldSwitch = listen<PlayerTeleportEvent>(priority = EventPriority.HIGHEST) {
        if (it.isCancelled) return@listen
        val fromWorld = it.from.world
        val toWorld = it.to.world
        if (fromWorld == toWorld) return@listen
        val fromCategory = WorldManager.getWorldData(fromWorld.uid)?.category ?: "Vanilla"
        val toCategory = WorldManager.getWorldData(toWorld.uid)?.category ?: "Vanilla"
        if (fromCategory == toCategory) return@listen
        val player = it.player
        savePlayerData(player, fromCategory)
        loadPlayerData(player, toCategory)?.let { loc -> it.to = loc }
    }


    /**
     * @param world world id that should be set
     * @param newCategory the new category name
     */
    fun setCategory(world: World, newCategory: String) {
        categories.forEach { (_, worlds) -> worlds.remove(world) } //Remove old category if present
        categories.getOrPut(newCategory) { mutableListOf() }.add(world)
    }

    /**
     * Load and set saved data to the player
     * @return Whether player data was found or not
     */
    fun loadPlayerData(player: Player, category: String): Location? {
        val data = playerData[player.uniqueId] ?: PlayerData(player.uniqueId, saveFolder)
        return data.loadData(player, category)
    }

    /**
     * Saves current player data to the current category
     */
    fun savePlayerData(player: Player, category: String) {
        val data = playerData.getOrPut(player.uniqueId) { PlayerData(player.uniqueId, saveFolder) }
        data.saveData(player, category)
    }

    /**
     * Save all current loaded data to disk and flush RAM.
     *
     * This could be a heavy call on big servers!
     */
    fun saveAll() {
        playerData.forEach { (_, data) -> data.saveToDisk() }
    }

    init {
        if (!saveFolder.exists()) saveFolder.mkdirs()
        taskRunLater(1) {
            worlds.forEach { setCategory(it, "Vanilla") }
            println(worlds.map { it.name })
            println(categories)
        }
    }
}