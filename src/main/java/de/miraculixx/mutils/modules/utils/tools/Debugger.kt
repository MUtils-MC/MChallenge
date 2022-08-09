package de.miraculixx.mutils.modules.utils.tools

import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class Debugger(private val player: Player) {

    private fun disable() {
        onClick.unregister()
    }

    private val onClick = listen<InventoryClickEvent> {
        if (it.whoClicked == player) {
            if (!player.scoreboardTags.contains("DEBUG")) disable()
            val item = it.currentItem
            if (item?.hasItemMeta() == true && item.itemMeta.hasCustomModelData())
                broadcast("§7Item ID: ${item.itemMeta.customModelData}")
        }
    }
}