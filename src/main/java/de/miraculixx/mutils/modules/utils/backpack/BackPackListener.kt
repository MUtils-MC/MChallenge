@file:Suppress("DEPRECATION", "unused")

package de.miraculixx.mutils.modules.utils.backpack

import de.miraculixx.mutils.system.config.Config
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.cropColor
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

object BackPackListener {
    private val map = HashMap<Player, String>()

    val onInvOpen = listen<InventoryOpenEvent> {
        if (it.view.title.contains("Backpack") && it.player is Player &&
            it.inventory.getItem(4)?.type == Material.PLAYER_HEAD
        ) {
            val name = it.inventory.getItem(4)!!.itemMeta?.displayName!!.cropColor()
            map[it.player as Player] = name
        }
    }

    val onInvClose = listen<InventoryCloseEvent> {
        if (it.view.title.contains("Backpack") &&
            it.inventory.getItem(4)?.type == Material.PLAYER_HEAD
        ) {
            val config = ConfigManager.getConfig(Configs.BACKPACK)
            val manager = BackPackManager(config)
            manager.saveInv(it.inventory)

            if (map.containsKey(it.player))
                map.remove(it.player)
        }
    }

    val onInvClick = listen<InventoryClickEvent> {
        if (it.view.title.contains("Backpack") && it.currentItem != null) {
            if (it.currentItem!!.hasItemMeta() &&
                it.currentItem!!.itemMeta?.hasCustomModelData() == true &&
                it.currentItem!!.itemMeta?.customModelData == 111
            )
                it.isCancelled = true
            else {
                taskRunLater(1) {
                    val file = Config("utils/backpack")
                    val manager = BackPackManager(file.getConfig())
                    val name = manager.saveInv(it.inventory)
                    map.forEach { (player, invName) ->
                        if (name == invName && it.whoClicked != player)
                            player.openInventory(manager.getBackPack(Bukkit.getOfflinePlayer(invName)))
                    }
                    file.save()
                }
            }
        }
    }
}