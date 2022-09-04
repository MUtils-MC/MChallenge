package de.miraculixx.mutils.modules.worldManager

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.player.PlayerPortalEvent

object WorldListener {
    private val onPortal = listen<PlayerPortalEvent> {
        if (ModuleManager.isActive(Modules.ONE_BIOME)) return@listen
        val worldName = it.from.world?.name ?: "world"
        if (it.to.world == null) {
            it.player.sendMessage(msg("module.worldManager.worldNotExist", it.player, "$worldName destination"))
            return@listen
        }
        val dName = worldName.split('_', limit = 2)[0] +
                when (it.to.world!!.environment) {
                    World.Environment.NORMAL -> ""
                    World.Environment.NETHER -> "_nether"
                    World.Environment.THE_END -> "_the_end"
                    World.Environment.CUSTOM -> "_custom"
                }
        val dWorld = Bukkit.getWorld(dName)
        if (dWorld == null) {
            it.player.sendMessage(msg("module.worldManager.worldNotExist", it.player))
            return@listen
        }
        it.to.world = dWorld
    }
}