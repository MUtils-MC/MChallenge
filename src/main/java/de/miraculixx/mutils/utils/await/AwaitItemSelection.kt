package de.miraculixx.mutils.utils.await

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.items.buildItem
import de.miraculixx.mutils.utils.text.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AwaitItemSelection(player: Player, filter: String, preset: GUI, callback: (ItemStack) -> Unit) {
    private val lore = listOf(emptyComponent(), cmp("Click", cHighlight) + cmp(" ≫ Choose Item"))
    private val invBuilder = GUIBuilder(player, preset, GUIAnimation.WATERFALL_OPEN)
        .storage(
            StorageFilter.HIDE,
            Material.values()
                .filter { (it.isItem && it.creativeCategory != null && it.name.contains(filter.uppercase().replace(' ', '_'))) || it == Material.STRUCTURE_VOID }
                .map { buildItem(it, -1, cmp(it.name.fancy(), cHighlight), lore) },
            buildItem(Material.OAK_SIGN, 0, cmp("Item Selection", cHighlight, bold = true), listOf(cmp("Search: $filter")))
        )

    init {
        invBuilder.open()
        AwaitInventoryClick(player, invBuilder.get()!!, callback)
    }
}