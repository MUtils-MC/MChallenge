@file:Suppress("DEPRECATION")

package de.miraculixx.mutils.utils.gui

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.items.ItemLib
import de.miraculixx.mutils.utils.gui.items.PDCValues
import de.miraculixx.mutils.utils.gui.items.editMeta
import de.miraculixx.mutils.utils.gui.items.skullTexture
import de.miraculixx.mutils.utils.text.*
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class GUIBuilder(private val player: Player, private val preset: GUI, private val animation: GUIAnimation = GUIAnimation.DEFAULT) {
    private var finalInv: Inventory? = null
    private val view: InventoryView = player.openInventory
    private lateinit var title: String

    private fun createInventory(size: Int? = null, title: String? = null): Inventory {
        val s = size ?: preset.size
        this.title = title ?: preset.title
        return Bukkit.createInventory(null, s * 9, this.title)
    }

    fun get(): Inventory? {
        return finalInv
    }

    fun open() {
        finalInv ?: return
        val transition = GUITransitions(player, title)
        when (animation) {
            GUIAnimation.MOVE_RIGHT -> transition.moveRight(view.topInventory, finalInv!!)
            GUIAnimation.MOVE_LEFT -> transition.moveLeft(view.topInventory, finalInv!!)
            GUIAnimation.MOVE_UP -> transition.moveUp(view.topInventory, finalInv!!)
            GUIAnimation.MOVE_DOWN -> transition.moveDown(view.topInventory, finalInv!!)

            else -> player.openInventory(finalInv ?: return)
        }
    }

    fun addIndicator(slot: Int, key: String, value: String): GUIBuilder {
        val namespacedKey = NamespacedKey(Manager, key)
        val newItem = finalInv?.getItem(slot)?.editMeta(dataContainer = PDCValues(namespacedKey, value)) ?: return this
        finalInv?.setItem(slot, newItem)
        return this
    }

    /**
     * Scrolling GUIs
     * - GUI for toggleable Settings. Scrolls smoothly forwards & backwards
     * @param change The amount of changed pages - negatives go backwards
     * @param import Imports a custom list on content
     * @param title Custom Title for variable Inventorys
     * @return Current GUI Builder Instance
     * @author Miraculixx
     */
    fun scroll(change: Int, import: Map<ItemStack, Boolean>? = null, title: String? = null): GUIBuilder {
        val lib = ItemLib()
        val list = when (preset) {
            GUI.CHALLENGE -> lib.getChallenge(1, null)
            GUI.TIMER_GOALS -> lib.getTimerRules(1, ConfigManager.getConfig(Configs.TIMER))
            GUI.TIMER_RULES -> lib.getTimerRules(2, ConfigManager.getConfig(Configs.TIMER))
            GUI.SPEEDRUN_SETTINGS -> lib.getSpeedrun(1, ConfigManager.getConfig(Configs.SPEEDRUN))
            GUI.WORLD_GLOBAL_SETTINGS -> lib.getWorld(2, c = ConfigManager.getConfig(Configs.WORLDS))
            else -> import ?: return this
        }
        finalInv = createInventory(4, title)

        // Placeholder
        finalInv?.fillPlaceholder()
        val phPrimary = InvUtils.primaryPlaceholder.type
        var ph = itemStack(Material.BARRIER) {
            meta {
                customModel = 0
                name = "§c§oLocked"
            }
        }
        for (i in 10..16) {
            finalInv!!.setItem(i, ph)
        }
        ph = itemStack(phPrimary) {
            meta {
                customModel = 200
                name = " "
                lore = ArrayList<String>().addLines("§7§m   §7 ${msg("item.GUI.Back", pre = false)} §m   ")
            }
        }
        for (i in 29..33) {
            finalInv!!.setItem(i, ph)
        }

        /*
        - Page Calculation -
        Every Challenge has an id -> ID - Page Number + 9
        Any Challenges with a final number id in 10-17 will be displayed, otherwise they
        will be got ignored.
         */
        val topInv = view.topInventory
        val page = if (topInv.type == InventoryType.CHEST) {
            val pItem = if (topInv.size == 9 * 4) topInv.getItem(31)
            else topInv.getItem(22)
            if (pItem != null && pItem.hasItemMeta()) {
                val i = (pItem.amount - 1) + change
                if (i < 0) 0 else i
            } else 0
        } else 0
        ph = itemStack(Material.KNOWLEDGE_BOOK) {
            meta {
                customModel = 201
                amount = page + 1
                name = "§9§lPage ${page + 1}"
                lore = getMessageList("modules.global.storageInv")
                    .toMutableList().addLines(" ", "§9Click §7≫ Switch")
            }
        }
        finalInv!!.setItem(31, ph)

        //Navigation
        var item = if (page <= 0) {
            //Red Left Scroller - Viewer reach the left End
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 0
                    name = "§c<§m   "
                    lore = getMessageList("item.GUI.NavigateNL")
                    skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0="
                    )
                }
            }
        } else {
            itemStack(Material.PLAYER_HEAD) {
                //Green Left Scroller
                meta<SkullMeta> {
                    customModel = 202
                    name = "§a<§m   "
                    lore = getMessageList("item.GUI.NavigateL")
                    skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU1MGI3Zjc0ZTllZDc2MzNhYTI3NGVhMzBjYzNkMmU4N2FiYjM2ZDRkMWY0Y2E2MDhjZDQ0NTkwY2NlMGIifX19"
                    )
                }
            }
        }
        finalInv!!.setItem(27, item)
        item = if ((list.size - 7 - page) < 0) {
            itemStack(Material.PLAYER_HEAD) {
                //Red Right Scroller -> Options Amount - 7 (Display Size) - Page = Left hidden Options
                meta<SkullMeta> {
                    customModel = 0
                    name = "§c§m   §c>"
                    lore = getMessageList("item.GUI.NavigateNR")
                    skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNmZTg4NDVhOGQ1ZTYzNWZiODc3MjhjY2M5Mzg5NWQ0MmI0ZmMyZTZhNTNmMWJhNzhjODQ1MjI1ODIyIn19fQ=="
                    )
                }
            }
        } else itemStack(Material.PLAYER_HEAD) {
            //Green Right Scroller
            meta<SkullMeta> {
                customModel = 203
                name = "§a§m   §a>"
                lore = getMessageList("item.GUI.NavigateR")
                skullTexture(
                    this,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzMzlmZjJlNTM0MmJhMThiZGM0OGE5OWNjYTY1ZDEyM2NlNzgxZDg3ODI3MmY5ZDk2NGVhZDNiOGFkMzcwIn19fQ=="
                )
            }
        }
        finalInv!!.setItem(35, item)

        var id = 10
        list.forEach { (item, active) ->
            if (id - page in 10..16) {
                finalInv!!.setItem(id - page, item)
                val p = if (active) itemStack(Material.LIME_STAINED_GLASS_PANE) {
                    meta {
                        customModel = item.itemMeta.customModelData
                        name = "§aActivated"
                    }
                }
                else itemStack(Material.RED_STAINED_GLASS_PANE) {
                    meta {
                        customModel = item.itemMeta.customModelData
                        name = "§cDeactivated"
                    }
                }
                finalInv!!.setItem(id - page + 9, p)
            }
            id++
        }
        return this
    }

    /**
     * Settings GUI
     * - A Simple 3 Rows GUI with 1-3 Items
     * @param page Page count of previous scrolling Inventory
     * @param import Imports a custom list on content
     * @return Current GUI Builder Instance
     * @author Miraculixx
     */
    fun settings(page: Int, import: List<ItemStack>? = null): GUIBuilder {
        val lib = ItemLib()
        val list = when (preset) {
            GUI.SETTINGS_IN_TIME -> lib.getSetting(1)
            GUI.SETTINGS_CAPTIVE -> lib.getSetting(2)
            GUI.SETTINGS_GHOST -> lib.getSetting(3)
            GUI.SETTINGS_NO_SAME_ITEM -> lib.getSetting(4)
            GUI.SETTINGS_BOOST_UP -> lib.getSetting(5)
            GUI.SETTINGS_CHUNK_BREAKER -> lib.getSetting(6)
            GUI.SETTINGS_DAMAGER -> lib.getSetting(7)
            GUI.SETTINGS_RIVAL_COLLECT -> lib.getSetting(8)
            else -> import ?: return this
        }
        if (list.isEmpty()) return this
        finalInv = createInventory()
        finalInv?.fillPlaceholder()
        finalInv!!.setItem(22, itemStack(Material.KNOWLEDGE_BOOK) {
            meta {
                amount = page + 1
                customModel = 201
                name = "§9§lPage ${page + 1}"
            }
        })

        /* Item Tree
        1 -> --oo1oo--
        2 -> --o1o2o--
        3 -> --1o2o3--
         */
        val s = list.size
        finalInv!!.setItem(14 - s, list[0])
        if (s >= 2) finalInv!!.setItem(16 - s, list[1])
        if (s >= 3) finalInv!!.setItem(18 - s, list[2])
        return this
    }

    fun storage(filterChange: StorageFilter?, import: List<ItemStack>?, header: ItemStack? = null): GUIBuilder {
        return storage(filterChange, buildMap {
            import?.forEach { item ->
                put(item, false)
            }
        }, header)
    }

    /**
     * Storage GUIs
     * - Compact Inventory with low amount of Placeholders and Design Items
     * @param filterChange Used filter for this instance. If null is imported the Builder trys to use the previous one
     * @param import Imports a custom list on content
     * @return Current GUI Builder Instance
     * @author Miraculixx
     */
    fun storage(filterChange: StorageFilter?, import: Map<ItemStack, Boolean>? = null, header: ItemStack? = null): GUIBuilder {
        val lib = ItemLib()
        var topItem: ItemStack? = header

        //Filter Calculation
        val filter = if (filterChange == null) {
            val preInv = view.topInventory
            if (preInv.size != 6 * 9) StorageFilter.NO_FILTER
            else {
                val meta = preInv.getItem(49)?.itemMeta
                val dataValue = meta?.persistentDataContainer?.getOrNull(NamespacedKey(Manager, "gui.storage.filter"))
                if (dataValue == null) StorageFilter.NO_FILTER
                else StorageFilter.valueOf(dataValue)
            }
        } else filterChange

        val map = when (preset) {
            GUI.WORLD_OVERVIEW -> {
                topItem = lib.getTopItem(1)
                lib.getWorld(1, player)
            }

            GUI.CHALLENGE -> {
                topItem = lib.getTopItem(2)
                lib.getChallenge(1, filter)
            }

            GUI.BANNED_PLAYERS -> {
                topItem = lib.getTopItem(3)
                mapConvert(lib.getServerSettings(2, null))
            }

            GUI.WHITELIST_PLAYERS -> {
                topItem = lib.getTopItem(4)
                mapConvert(lib.getServerSettings(3, null))
            }

            GUI.WORLD_GLOBAL_SETTINGS -> {
                topItem = lib.getTopItem(5)
                lib.getWorld(2, c = ConfigManager.getConfig(Configs.WORLDS))
            }

            else -> import ?: return this
        }
        finalInv = createInventory(6)

        //Placeholder (Design)
        val ph2 = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
            meta {
                customModel = 0
                name = " "
            }
        }
        finalInv?.fillPlaceholder()
        finalInv?.setItem(4, topItem ?: InvUtils.primaryPlaceholder)
        if (filterChange != StorageFilter.HIDE) {
            finalInv?.setItem(49,  itemStack(Material.HOPPER) {
                meta {
                    customModel = 205
                    name = "§9§lFilters"
                    lore = getMessageList("item.GUI.Filter").toMutableList()
                        .addLines(
                            " ", "§9§nFilter", "§7∙ ${filter.name.replace('_', ' ')}",
                            " ", "§9Click §7≫ Rotate Filter"
                        )
                    persistentDataContainer.set(NamespacedKey.fromString("gui.storage.filter", Manager)!!, PersistentDataType.STRING, filter.name)
                }
            })
            repeat(9 * 4) { i ->
                finalInv?.setItem(i + 9, ph2)
            }
        } else {
            repeat(9 * 5) { i ->
                finalInv?.setItem(i + 9, ph2)
            }
        }


        //Content
        var counter = 0
        map.forEach { (item, activated) ->
            if (activated) {
                val type = item.type
                if (type == Material.PLAYER_HEAD || type == Material.ZOMBIE_HEAD || type == Material.SKELETON_SKULL || type == Material.CHEST)
                    item.type = Material.LIME_STAINED_GLASS_PANE
                val meta = item.itemMeta
                meta.addEnchant(Enchantment.MENDING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                item.itemMeta = meta
            }
            if (counter + 9 >= (finalInv?.size?.minus( if (filterChange == StorageFilter.HIDE) 0 else 9) ?: 0)) return this
            finalInv?.setItem(9 + counter, item)
            counter++
        }
        return this
    }

    /**
     * Custom GUIs
     * - Complete custom Inventory with no specific design
     * @param fill Should the Inventory be filled with default Placeholders?
     * @param import Imports a custom list on content
     * @param size Override the default Inventory size
     * @param title Override the default Inventory title
     * @return Current GUI Builder Instance
     * @author Miraculixx
     */
    fun custom(fill: Boolean = true, import: Map<ItemStack, Int>? = null, size: Int? = null, title: String? = null): GUIBuilder {
        val lib = ItemLib()
        val list = when (preset) {
            GUI.SELECT_MENU -> lib.getMain(1)
            GUI.SPEC_SETTINGS -> lib.getSpec(1, Spectator.getSettings(player.uniqueId))
            GUI.SPEC_TROLL -> lib.getSpec(2)
            GUI.SPEC_TROLL_SOUNDS -> lib.getSpec(3)
            GUI.SPEC_TROLL_BLOCKS -> lib.getSpec(4)
            GUI.TIMER_SETTINGS -> lib.getTimer(1, ModuleManager.getTime())
            GUI.TIMER_DESIGN -> lib.getTimer(2, title = ModuleManager.getTime(true))
            GUI.WORLD_MAIN -> lib.getMain(2)
            GUI.SERVER_SETTINGS -> lib.getServerSettings(1, ConfigManager.getConfig(Configs.SETTINGS))
            GUI.CREATOR_MAIN -> lib.getMain(3)
            else -> import ?: return this
        }
        finalInv = createInventory(size, title)
        if (fill) finalInv?.fillPlaceholder()
        list.forEach { (a, b) ->
            finalInv!!.setItem(b, a)
        }
        return this
    }

    /**
     * Player Inventory
     * - Modify the players Inventory instead of open a new one
     * - Notes: OffHand - 40 | Hotbar - 0-8 | Boots-Helmet - 36-39
     * @param import Imports a custom list on content
     * @author Miraculixx
     */
    fun player(import: Map<ItemStack, Int>? = null) {
        val lib = ItemLib()
        val list = when (preset) {
            GUI.SPEC_HOTBAR -> lib.getSpec(5, b = true)
            GUI.SPEC_HOTBAR_QUICK -> lib.getSpec(5, b = false)
            else -> import ?: return
        }
        val inv = player.inventory
        list.forEach { (item, i) ->
            inv.setItem(i, item)
        }
    }

    /*
    GUI Utilitys
     */
    private fun mapConvert(map: Map<ItemStack, *>): LinkedHashMap<ItemStack, Boolean> {
        val dummyMap = LinkedHashMap<ItemStack, Boolean>()
        map.keys.forEach {
            dummyMap[it] = false
        }
        return dummyMap
    }
}