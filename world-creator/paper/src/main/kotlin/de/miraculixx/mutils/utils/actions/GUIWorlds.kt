package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.soundError
import de.miraculixx.mutils.extensions.toUUID
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsMenu
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.time.Duration.Companion.seconds

class GUIWorlds : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta ?: return@event

        if (meta.customModel == 1) {
            val worldID = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.worlds.uuid"), PersistentDataType.STRING)
            val uuid = worldID?.toUUID()
            val world = uuid?.let { it1 -> Bukkit.getWorld(it1) }
            if (world == null) {
                player.soundError()
                player.sendMessage(prefix + cmp("Failed to resolve world with ID $worldID!", cError))
            } else {
                player.closeInventory()
                player.title(emptyComponent(), cmp(msgString("event.teleportToWorld", listOf(world.name)), cHighlight), fadeOut = 0.5.seconds)
                player.teleport(world.spawnLocation)
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            }
        } else {
            GUITypes.WORLD_MENU.buildInventory(player, "WORLD_MENU", ItemsMenu(), GUIMenu())
            player.click()
        }
    }
}