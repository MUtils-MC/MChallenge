package de.miraculixx.mchallenge.modules.global

import de.miraculixx.kpaper.event.listen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.resource.ResourcePackInfo
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import java.net.URI
import java.util.*

object ResourcePackManager {
    private val loadedPacks = mutableMapOf<UUID, ResourcePackInfo>() // PackId -> ResourcePackInfo
    private val playerLoaded = mutableMapOf<UUID, MutableSet<UUID>>() // Player -> Set<ResourcePackInfo>

    /**
     * Load a resource pack for target players. Those will persist through rejoins.
     */
    fun loadPack(packID: UUID, targets: Collection<Player>): Boolean {
        val packInfo = loadedPacks[packID] ?: return false
        targets.forEach { p ->
            p.sendResourcePacks(packInfo)
            playerLoaded.getOrPut(p.uniqueId) { mutableSetOf() }.add(packID)
        }
        return true
    }

    /**
     * Unloads a pack for all players. If remove is true, the pack will be removed from the available loader list.
     */
    fun unloadPack(packID: UUID, remove: Boolean = false) {
        playerLoaded.forEach { (playerID, packs) ->
            if (packs.remove(packID)) Bukkit.getPlayer(playerID)?.removeResourcePack(packID)
        }
    }

    fun addPack(packInfo: ResourcePackInfo) {
        loadedPacks[packInfo.id()] = packInfo
    }

    // Reapply packs on join
    private val onJoin = listen<PlayerJoinEvent> {
        val packs = playerLoaded[it.player.uniqueId]?.mapNotNull { packID -> loadedPacks[packID] } ?: return@listen
        packs.forEach { pack ->
            it.player.sendResourcePacks(pack)
        }
    }

    enum class Pack(val uuid: UUID) {
        HIDE_BOSS_BAR(UUID.randomUUID()),
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            addPack(ResourcePackInfo.resourcePackInfo()
                .id(Pack.HIDE_BOSS_BAR.uuid)
                .uri(URI("https://cdn.modrinth.com/data/sRddCRho/versions/4g3ZPiy5/Invisible%20Bossbar%20%28all%29.zip"))
                .computeHashAndBuild().get())

        }
    }
}