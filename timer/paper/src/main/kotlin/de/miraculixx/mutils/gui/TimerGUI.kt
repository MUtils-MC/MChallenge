package de.miraculixx.mutils.gui

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

enum class TimerGUI(private val title: Component) {
    RULES(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Rules", cHighlight)),
    GOALS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Goals", cHighlight)),
    DESIGN_PART_EDITOR(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Design Editor", cHighlight)),
    DESIGN_EDITOR(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Design Editor", cHighlight)),
    DESIGN(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Design", cHighlight)),
    OVERVIEW(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer", cHighlight));

    fun buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
        InventoryManager.get(id)?.open(player) ?: when (this) {
            OVERVIEW -> InventoryManager.inventoryBuilder(id) {
                this.size = 4
                this.title = this@TimerGUI.title
                this.player = player
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            DESIGN -> InventoryManager.storageBuilder(id) {
                this.title = this@TimerGUI.title
                this.player = player
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
                this.header = itemStack(Material.PLAYER_HEAD) {
                    meta {
                        name = cmp(msgString("items.createDesign.n"), cHighlight)
                        lore(msgList("items.createDesign.l", inline = "<grey>"))
                        customModel = 1
                    }
                    itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.PLUS_GREEN.value)
                }
            }

            DESIGN_EDITOR -> InventoryManager.inventoryBuilder(id) {
                this.title = this@TimerGUI.title
                this.player = player
                this.size = 3
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            DESIGN_PART_EDITOR -> InventoryManager.inventoryBuilder(id) {
                this.title = this@TimerGUI.title
                this.player = player
                this.size = 4
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }

            GOALS, RULES -> InventoryManager.scrollBuilder(id) {
                this.title = this@TimerGUI.title
                this.player = player
                this.itemProvider = itemProvider
                this.clickAction = clickAction.run
            }
        }
    }
}