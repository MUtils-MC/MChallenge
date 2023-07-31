package de.miraculixx.mcore.await

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.extensions.soundStone
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.data.InventoryManager
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.extensions.lore
import de.miraculixx.mvanilla.extensions.name
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

/**
 * gui.await.sound
 */
class AwaitSoundSelections(player: Player, filter: String, random: Boolean, callback: (Sound?) -> Unit) {
    private val lore = listOf(emptyComponent(), msgClick + cmp("Choose Sound"))

    class Items(private val filter: String, private val lore: List<Component>) : ItemProvider {
        override fun getItemList(from: Int, to: Int): List<ItemStack> {
            return Sound.values().filter { it.name.contains(filter.replace(' ', '_'), ignoreCase = true) }
                .map {
                    itemStack(Material.JUKEBOX) {
                        meta {
                            name = cmp(it.name.replace('_', ' '), cHighlight)
                            customModel = 1
                            lore(this@Items.lore)
                            persistentDataContainer.set(NamespacedKey(namespace, "gui.await.sound"), PersistentDataType.STRING, it.name)
                        }
                    }
                }
        }
    }

    init {
        val key = NamespacedKey(namespace, "gui.await.sound")
        InventoryManager.storageBuilder(player.uniqueId.toString()) {
            title = cmp("Select Sound", cHighlight)
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
                    1 -> callback.invoke(enumOf<Sound>(item.itemMeta?.persistentDataContainer?.get(key, PersistentDataType.STRING)))
                    2 -> callback.invoke(null)
                    else -> player.soundStone()
                }
            }
            closeAction = event@{ _: InventoryCloseEvent, _: CustomInventory ->
                callback.invoke(null)
            }
            itemProvider = AwaitMobSelection.Items(filter, lore)
        }
    }
}