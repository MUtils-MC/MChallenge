package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.await.AwaitConfirm
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.items.ItemFilterProvider
import de.miraculixx.mcore.utils.checkPermission
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.WorldFilter
import de.miraculixx.mutils.utils.items.ItemsCopy
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.time.Duration.Companion.seconds

class GUIWorlds(private val preInv: CustomInventory?) : GUIEvent {
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

                when (it.click) {
                    ClickType.LEFT -> {
                        if (!player.checkPermission("mutils.event.tp")) return@event
                        player.closeInventory()
                        player.title(emptyComponent(), cmp(msgString("event.teleportToWorld", listOf(world.name)), cHighlight), fadeOut = 0.5.seconds)
                        player.teleport(world.spawnLocation)
                        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                    }

                    ClickType.RIGHT -> {
                        if (!player.checkPermission("mutils.event.create")) return@event
                        GUITypes.WORLD_COPY.buildInventory(player, "${player.uniqueId}-copy", ItemsCopy(), GUICopy(world, inv))
                    }

                    ClickType.SHIFT_RIGHT -> {
                        if (!player.checkPermission("mutils.event.delete")) return@event
                        if (worlds.indexOf(world) in 0..2) {
                            player.soundError()
                            player.sendMessage(msg("event.cannotDeleted"))
                            return@event
                        }
                        AwaitConfirm(player, {
                            WorldManager.deleteWorld(world.uid)
                            player.soundDelete()
                            inv.update()
                            inv.open(player)
                        }) {
                            player.soundDisable()
                            inv.open(player)
                        }
                    }

                    else -> Unit
                }
            }
        } else if (meta.customModel == 9005) {
            val provider = inv.itemProvider as ItemFilterProvider

            provider.filter = arrayOf(WorldFilter.NO_FILTER, WorldFilter.OVERWORLD, WorldFilter.NETHER, WorldFilter.END).enumRotate(enumOf<WorldFilter>(provider.filter) ?: WorldFilter.NO_FILTER).name
            player.soundUp()
            inv.update()
        } else if (preInv != null) {
            preInv.open(player)
            player.click()
        }
    }
}