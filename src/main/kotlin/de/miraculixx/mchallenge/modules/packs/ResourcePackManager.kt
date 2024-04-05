package de.miraculixx.mchallenge.modules.packs

import de.miraculixx.kpaper.extensions.onlinePlayers
import net.kyori.adventure.resource.ResourcePackInfo
import org.bukkit.entity.Player
import java.net.URI

object ResourcePackManager {

    fun sendToAll(resourcePack: ResourcePacks): Boolean {
        val packData = resolveResourcePack(resourcePack) ?: return false
        onlinePlayers.forEach { sendTo(resourcePack, it) }
        return true
    }

    fun removeFromAll(resourcePack: ResourcePacks) {
        onlinePlayers.forEach { it.removeResourcePacks(resourcePack.uuid) }
    }

    fun sendTo(resourcePack: ResourcePacks, player: Player): Boolean {
        val packData = resolveResourcePack(resourcePack) ?: return false
        sendTo(packData, player)
        return true
    }

    fun removeFrom(resourcePack: ResourcePacks, player: Player) {
        player.removeResourcePacks(resourcePack.uuid)
    }

    private fun sendTo(packData: ResourcePackInfo, player: Player) {
        player.sendResourcePacks(packData)
    }

    private fun resolveResourcePack(pack: ResourcePacks): ResourcePackInfo? {
        val file = pack.versionData?.files?.first { it.primary } ?: return null
        return ResourcePackInfo.resourcePackInfo(pack.uuid, URI(file.url), file.hashes.sha1)
    }
}