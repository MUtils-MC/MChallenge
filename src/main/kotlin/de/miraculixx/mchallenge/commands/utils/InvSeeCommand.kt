package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.mchallenge.utils.command
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.Sound
import org.bukkit.entity.Player

class InvSeeCommand {
    @Suppress("unused")
    val reset = commandTree("invsee") {
        withPermission("mutils.invsee")
        playerArgument("player") {
            playerExecutor { player, args ->
                val target = args[0] as? Player ?: return@playerExecutor
                player.openInventory(target.inventory)
                player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
            }
        }
    }
}