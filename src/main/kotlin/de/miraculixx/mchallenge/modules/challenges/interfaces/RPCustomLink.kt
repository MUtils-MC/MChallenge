package de.miraculixx.mchallenge.modules.challenges.interfaces

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.global.ResourcePackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.resource.ResourcePackInfo
import java.net.URI
import java.util.UUID

interface RPCustomLink {
    val rpLink: String
    val rpUUID: UUID

    fun sendCustomLinkPack() {
        CoroutineScope(Dispatchers.Default).launch {
            ResourcePackManager.addPack(
                ResourcePackInfo.resourcePackInfo()
                    .id(rpUUID)
                    .uri(URI(rpLink))
                    .computeHashAndBuild().get()
            )

            ResourcePackManager.loadPack(rpUUID, onlinePlayers)
        }
    }

    fun removeCustomLinkPack() {
        ResourcePackManager.unloadPack(rpUUID, true)
    }
}