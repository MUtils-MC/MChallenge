package de.miraculixx.api

import de.miraculixx.api.data.WorldData
import java.util.*

/**
 * Access to the MWorld Manager API
 */
abstract class MWorldAPI {

    companion object {
        var instance: MWorldAPI? = null
    }

    /**
     * Create a new custom world form a [WorldData] object.
     * All worlds created by this function will be saved and loaded automatically on server/client restart.
     * @return UUID of the created world or null if something failed
     */
    abstract fun createWorld(worldData: WorldData): UUID?

    /**
     * Get the [WorldData] object from a custom world.
     * Returns <b>null</b> if the [UUID] is invalid or does not belong to a custom world or to a vanilla world
     */
    abstract fun getWorldData(uuid: UUID): WorldData?

    /**
     * Get all loaded custom worlds with their [UUID]
     */
    abstract fun getLoadedWorlds(): Map<UUID, WorldData>

    /**
     * Copy an existing world - Only World Settings
     * @param [worldID] World [UUID] (source)
     * @param [name] New world name
     * @return UUID of the created world or null if something failed
     */
    abstract fun copyWorld(worldID: UUID, name: String): UUID?

    /**
     * Copy an existing world with all world data
     * @param [worldID] World [UUID] (source)
     * @param [name] New world name
     * @return UUID of the created world or null if something failed
     */
    abstract fun fullCopyWorld(worldID: UUID, name: String): UUID?

    /**
     * Unload and delete an existing world. Only works on custom worlds, not on vanilla worlds!
     * @param worldID World [UUID]
     */
    abstract fun deleteWorld(worldID: UUID)
}