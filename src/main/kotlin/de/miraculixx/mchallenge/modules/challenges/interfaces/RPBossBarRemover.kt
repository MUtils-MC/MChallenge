package de.miraculixx.mchallenge.modules.challenges.interfaces

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.global.ResourcePackManager

interface RPBossBarRemover {

    fun sendHideBossBar() {
        ResourcePackManager.loadPack(ResourcePackManager.Pack.HIDE_BOSS_BAR.uuid, onlinePlayers)
    }

    fun sendShowBossBar() {
        ResourcePackManager.unloadPack(ResourcePackManager.Pack.HIDE_BOSS_BAR.uuid)
    }
}