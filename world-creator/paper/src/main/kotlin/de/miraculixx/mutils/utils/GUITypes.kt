package de.miraculixx.mutils.utils

import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.cHighlight
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.plus
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

enum class GUITypes(private val title: Component) {
    WORLD_CREATOR_SETTINGS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("World Creator")),
    WORLD_CREATOR_ALGOS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("World Creator - Noise", cHighlight)),
    WORLD_MENU(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Worlds", cHighlight)),
    WORLD_OVERVIEW(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("World Overview", cHighlight)),
    WORLD_CREATOR(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("World Creator", cHighlight));

    fun buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
        InventoryManager.get(id)?.open(player) ?: when (this) {
            WORLD_MENU -> InventoryManager.inventoryBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.size = 3
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            WORLD_CREATOR, WORLD_CREATOR_SETTINGS -> InventoryManager.inventoryBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.size = 5
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            WORLD_OVERVIEW -> InventoryManager.storageBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.filterable = true
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            WORLD_CREATOR_ALGOS -> InventoryManager.storageBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }
        }
    }
}