package de.miraculixx.mutils.utils.await

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.items.PDCValues
import de.miraculixx.mutils.utils.gui.items.buildItem
import de.miraculixx.mutils.utils.text.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * gui.await.sound
 */
class AwaitSoundSelections(player: Player, filter: String, preset: GUI, callback: (ItemStack) -> Unit) {
    private val lore = listOf(emptyComponent(), cmp("Click", cHighlight) + cmp(" ≫ Choose Sound"))
    private val invBuilder = GUIBuilder(player, preset, GUIAnimation.WATERFALL_OPEN)
        .storage(
            StorageFilter.HIDE,
            buildList {
                addAll(Sound.values()
                    .filter { it.name.contains(filter.uppercase().replace(' ', '_')) }
                    .map { buildItem(Material.JUKEBOX, -1, cmp(it.name.fancy()), lore, values = listOf(PDCValues(NamespacedKey(Manager, "gui.await.sound"), it.name))) }
                )
                if (isEmpty()) add(buildItem(Material.STRUCTURE_VOID, -1, cmp("No Sound Found", cHighlight), lore))
            },
            buildItem(
                Material.OAK_SIGN, 0, cmp("Sound Selection", cHighlight, bold = true), listOf(cmp("Search: $filter")),
                values = listOf(PDCValues(Sound.ENTITY_ENDERMAN_TELEPORT.key, Sound.ENTITY_ENDERMAN_TELEPORT.name))
            )
        )

    init {
        invBuilder.open()
        AwaitInventoryClick(player, invBuilder.get()!!, callback)
    }
}