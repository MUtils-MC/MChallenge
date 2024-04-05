package de.miraculixx.mchallenge.modules.mods.worldChanging.lowVision

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mchallenge.gui.GUITypes
import de.miraculixx.mchallenge.gui.buildInventory
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.ResourcePackChallenge
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mcommons.text.consoleAudience
import de.miraculixx.mweb.api.MWebAPI
import net.kyori.adventure.audience.Audience
import org.bukkit.Sound

class LowVision : Challenge, ResourcePackChallenge {
    private lateinit var mWebAPI: MWebAPI
    private val maxSelects: Int
//    private val playerCache: MutableMap<UUID, Set<Material>>

    init {
        val settings = challenges.getSetting(Challenges.LOW_VISION).settings
        maxSelects = settings["amount"]?.toInt()?.getValue() ?: 5
    }

    override fun start(): Boolean {
        if (!PluginManager.server.pluginManager.isPluginEnabled("MUtils-Web")) return error(Audience.audience(onlinePlayers + consoleAudience))
        MWebAPI.INSTANCE?.let { mWebAPI = it } ?: return error(Audience.audience(onlinePlayers + consoleAudience))

        onlinePlayers.forEach { player ->
            GUITypes.CH_LOW_VISION.buildInventory(player, "${player.uniqueId}-LOWVISION", LowVisionItems(player.language()), LowVisionAction(maxSelects, player, mWebAPI))
            player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 0.6f, 1f)
        }
        return true
    }

    override fun register() {}

    override fun unregister() {}
}