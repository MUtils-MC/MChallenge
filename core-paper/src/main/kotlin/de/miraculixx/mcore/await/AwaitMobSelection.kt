package de.miraculixx.mcore.await

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.extensions.soundStone
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.data.InventoryManager
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mcore.utils.getLivingMobs
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

/**
 * gui.await.mob
 */
class AwaitMobSelection(player: Player, filter: String, random: Boolean, callback: (EntityType?) -> Unit) {
    private val lore = listOf(emptyComponent(), msgClick + cmp("Choose Mob"))

    class Items(private val filter: String, private val lore: List<Component>) : ItemProvider {
        override fun getItemList(from: Int, to: Int): List<ItemStack> {
            return getLivingMobs(false).filter { it.name.contains(filter.replace(' ', '_'), ignoreCase = true) }
                .map {
                    itemStack(enumOf<Material>("${it.name}_SPAWN_EGG") ?: Material.POLAR_BEAR_SPAWN_EGG) {
                        meta {
                            name = cmp(it.name.replace('_', ' '), cHighlight)
                            customModel = 1
                            lore(this@Items.lore)
                            persistentDataContainer.set(NamespacedKey(namespace, "gui.await.mob"), PersistentDataType.STRING, it.name)
                        }
                    }
                }
        }
    }

    init {
        val key = NamespacedKey(namespace, "gui.await.mob")
        InventoryManager.storageBuilder(player.uniqueId.toString()) {
            title = cmp("Select Mob", cHighlight)
            header = itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp("Cancel", cError)
                    customModel = 2
                    lore(listOf(cmp("Search: $filter")))
                }
                (itemMeta as SkullMeta).skullTexture(Head64.X_RED.value)
            }
            scrollable = true
            clickAction = event@{ it: InventoryClickEvent, _: CustomInventory ->
                it.isCancelled = true
                val item = it.currentItem
                when (item?.itemMeta?.customModel) {
                    1 -> callback.invoke(enumOf<EntityType>(item.itemMeta?.persistentDataContainer?.get(key, PersistentDataType.STRING)))
                    2 -> callback.invoke(null)
                    else -> player.soundStone()
                }
            }
            closeAction = event@{ _: InventoryCloseEvent, _: CustomInventory ->
                callback.invoke(null)
            }
            itemProvider = Items(filter, lore)
        }
    }
}