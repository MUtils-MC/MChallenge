package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.api.data.enums.GeneratorAlgorithm
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class ItemsNoiseAlgos: ItemProvider {
    private val infoLore = listOf(emptyComponent(), cmp("• ") + cmp("Info", cHighlight, underlined = true))
    private val clickLore = listOf(emptyComponent(), msgClick + cmp("Add Algorithm"))

    override fun getItemList(): List<ItemStack> {
        return GeneratorAlgorithm.values().map {
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 1
                    name = cmp(msgString("items.algo.${it.name}.n"), cHighlight)
                    lore(infoLore + msgList("items.algo.${it.name}.l") + clickLore)
                    persistentDataContainer.set(NamespacedKey(namespace, "gui.wc.noise"), PersistentDataType.STRING, it.name)
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(it.icon)
            }
        }
    }
}