package de.miraculixx.mutils.utils.tools.gui

import de.miraculixx.mutils.modules.gui.GUITools
import de.miraculixx.mutils.utils.getComponentList
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.emptyComponent
import de.miraculixx.mutils.utils.text.plus
import net.axay.kspigot.event.listen
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ScrollGUI(
    private val player: Player,
    private val items: HashMap<ItemStack, Boolean>,
    private val header: ItemStack,
    private val title: Component,
    startState: State,
    config: FileConfiguration? = null
) {
    private val tools = GUITools(config)
    private var state = startState
    private var gui: Inventory

    private val listenClick = listen<InventoryClickEvent> {
        if (it.whoClicked !is Player) return@listen
        val player = it.whoClicked
        val item = it.currentItem
        when (item?.itemMeta?.customModelData) {

        }
    }

    private fun craftGUI(): Inventory {
        return when (state) {
            State.SCROLL_MENU -> {
                val inv = Bukkit.createInventory(null, 4, title).fillPlaceholder()
                inv
            }
            State.STORAGE_MENU -> {
                val inv = Bukkit.createInventory(null, 6, title).fillPlaceholder()
                val ph = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                    meta {
                        customModel = 0
                        name = " "
                    }
                }
                inv.setItem(4, header)
                inv.setItem(49, itemStack(Material.HOPPER) {
                    meta {
                        customModel = 205
                        name = "§9§lFilters"
                        lore(buildList {
                            addAll(getComponentList("item.GUI.Filter"))
                            addAll(listOf(emptyComponent(),
                                cmp("Filter", cHighlight, underlined = true),
                                cmp("∙ ${/*filter.name.replace('_', ' ')*/"Filter"}"),
                                emptyComponent(),
                                cmp("Click", cHighlight) + cmp("≫ Rotate Filter")
                            ))
                            add(Component.text("∙ "))
                        })
                    }
                })
                repeat(9 * 4) { i ->
                    inv.setItem(i + 9, ph)
                }
                inv
            }
        }
    }

    // 205 - Hopper Filter
    private fun craftItem(): ItemStack {
        return ItemStack(Material.HOPPER)
    }

    init {
        gui = craftGUI()
    }


    enum class Item {
        NAV_RIGHT, NAV_LEFT, SCROLL_TO_STORAGE, STORAGE_TO_SCROLL, FILTER
    }

    enum class State {
        SCROLL_MENU, STORAGE_MENU
    }
}