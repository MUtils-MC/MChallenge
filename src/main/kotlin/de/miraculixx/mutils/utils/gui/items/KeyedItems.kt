package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.text.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class KeyedItems {
    fun getItems(id: Int, key: String, filter: String?): List<ItemStack> {
        return when (id) {
            1 -> g1(key, filter ?: "AIR")

            else -> emptyList()
        }
    }

    private fun g1(key: String, filter: String) : List<ItemStack> {
        val lore = listOf(emptyComponent(), cmp("Click", cHighlight) + cmp(" ≫ Select Option"))
        // - x - - 1 2 3 4 -
        return listOf(
            InvUtils.secondaryPlaceholder,
            buildItem(
                Material.STRUCTURE_VOID, -1, cmp("Select Nothing", cHighlight), lore,
                values = listOf(PDCValues(NamespacedKey(Manager, key), "ENTITY_ENDERMAN_TELEPORT"))
            ),
            InvUtils.primaryPlaceholder, InvUtils.primaryPlaceholder.clone().editMeta(cmp("  ")),
            buildItem(
                Material.PLAYER_HEAD, -1, cmp("Random (onetime)", cHighlight), buildList {
                    addAll(getComponentList("item.await.rndOnetime", inline = ""))
                    addAll(lore)
                }, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUyMmMyOThlN2M2MzM2YWYxNzkwOWFjMWYxZWU2ODM0YjU4YjFhM2NjOTlhYmEyNTVjYTdlYWViNDc2MTczIn19fQ==",
                listOf(PDCValues(NamespacedKey(Manager, key), "#random-onetime"))
            ),
            buildItem(
                Material.PLAYER_HEAD, -1, cmp("Random Filter (onetime)", cHighlight), buildList {
                    addAll(getComponentList("item.await.rndOnetimeFilter", inline = ""))
                    addAll(lore)
                }, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiN2E3M2ZjOTM0YzlkZTkxNjBjMGZkNTlkZjZlNDJlZmQ1ZDAzNzhlMzQyYjY4NjEyY2ZlYzNlODk0ODM0YSJ9fX0=",
                listOf(PDCValues(NamespacedKey(Manager, key), "#random-onetime-filter"))
            ),
            buildItem(
                Material.PLAYER_HEAD, -1, cmp("Random (everytime)", cHighlight), buildList {
                    addAll(getComponentList("item.await.rndEverytime", inline = ""))
                    addAll(lore)
                }, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI0MGU1ZGIyMWNlZGFjNGM5NzJiN2IyMmViYjY0Y2Y0YWRkNjFiM2I1NGIxMzE0MzVlZWRkMzA3NTk4YjcifX19",
                listOf(PDCValues(NamespacedKey(Manager, key), "#random-everytime"))
            ),
            buildItem(
                Material.PLAYER_HEAD, -1, cmp("Random Filter (everytime)", cHighlight), buildList {
                    addAll(getComponentList("item.await.rndEverytimeFilter", inline = ""))
                    addAll(lore)
                }, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRlZmIzNDQxN2Q5NWZhYTk0ZjI1NzY5YTIxNjc2YTAyMmQyNjMzNDZjODU1M2ViNTUyNTY1OGIzNDI2OSJ9fX0=",
                listOf(PDCValues(NamespacedKey(Manager, key), "#random-everytime-filter:$filter"))
            ), InvUtils.secondaryPlaceholder.clone().editMeta(cmp("  "))
        )
    }
}