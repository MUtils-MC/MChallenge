package de.miraculixx.mutils.commands

import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.actions.GUIWorlds
import de.miraculixx.mutils.utils.items.ItemsWorlds
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.Sound

class WorldsCommand {
    private val command = commandTree("worlds") {
        playerExecutor { player, _ ->
            GUITypes.WORLD_OVERVIEW.buildInventory(player, "${player.uniqueId}-OVERVIEW", ItemsWorlds(player.world.uid), GUIWorlds(null))
            player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
        }
    }
}