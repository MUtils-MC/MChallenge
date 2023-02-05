package de.miraculixx.mutils.utils.gui

import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.cHighlight
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.messages.plus
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

enum class GUITypes(private val title: Component) {
    CHALLENGE_SETTINGS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Challenge Settings", cHighlight)),
    SPEC_SETTINGS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Settings", cHighlight)),
    SPEC_PLAYER_OVERVIEW(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Online Player", cHighlight)),
    CHALLENGE_MENU(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Challenges", cHighlight));

    fun buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
        InventoryManager.get(id)?.open(player) ?: when (this) {
            CHALLENGE_MENU -> InventoryManager.scrollBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.dataKeys = listOf(NamespacedKey(namespace, "gui.challenge"))
                this.filterable = true
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            SPEC_PLAYER_OVERVIEW -> InventoryManager.storageBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            SPEC_SETTINGS -> InventoryManager.inventoryBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.size = 3 * 9
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            CHALLENGE_SETTINGS -> InventoryManager.settingsBuilder(id) {
                this.title = this@GUITypes.title
                this.player = player
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }
        }
    }
}