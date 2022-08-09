package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.timer.Weather
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.enums.settings.gui.StorageFilters
import de.miraculixx.mutils.modules.worldManager.WorldCreator
import de.miraculixx.mutils.modules.worldManager.WorldTools
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.error
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import de.miraculixx.mutils.utils.tools.gui.items.ItemLib
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.items.customModel
import org.bukkit.Difficulty
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class WorldGUI(private val it: InventoryClickEvent, private val player: Player, title: String) {
    private val item = it.currentItem
    private val id = item?.itemMeta?.customModel

    init {
        when (title) {
            GUI.WORLD_MAIN.title -> main()
            GUI.WORLD_OVERVIEW.title -> overview()
            GUI.WORLD_GLOBAL_SETTINGS.title -> globalSettings()
            else -> {}
        }
    }

    private fun globalSettings() {
        val c = ConfigManager.getConfig(Configs.WORLDS)
        val tool = GUITools(c)
        val click = it.click
        when (id) {
            200 -> {
                GUIBuilder(player, GUI.WORLD_MAIN, GUIAnimation.SPLIT).custom().open()
                player.click()
                return
            }
            205 -> {
                player.sendMessage("$prefix§c No Filter available!")
                player.error()
            }
            203 -> {
                if (click.isShiftClick)
                    tool.navigate(player, 5, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                else tool.navigate(player, 1, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                return
            }
            202 -> {
                if (click.isShiftClick)
                    tool.navigate(player, -5, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                else tool.navigate(player, -1, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                return
            }
            201 -> {
                player.click()
                GUIBuilder(player, GUI.WORLD_GLOBAL_SETTINGS).storage(null).open()
                return
            }

            100 -> if (click.isRightClick) {
                val items = ItemLib().getWorld(3, c = ConfigManager.getConfig(Configs.BACKPACK)).keys.toList()
                GUIBuilder(player, GUI.CUSTOM).settings(1, items).open()
            } else tool.toggleSetting(player, "Global.Backpack")
            101 -> tool.toggleSetting(player, "Global.HeartsTab")
            102 -> tool.toggleSetting(player, "Global.Hardcore")
            103 -> if (click.isRightClick)
                tool.enumRotate(Difficulty.values().toList(), Difficulty.valueOf(c.getString("Global.DifficultyMode") ?: "EASY"), player)
            else tool.toggleSetting(player, "Global.Difficulty")
            104 -> tool.toggleSetting(player, "Global.TimeFreeze")
            105 -> if (click.isRightClick)
                tool.enumRotate(Weather.values().toList(), Weather.valueOf(c.getString("Global.WeatherMode") ?: "SUNNY"), player)
            else tool.toggleSetting(player, "Global.Weather")
            106 -> tool.toggleSetting(player, "Global.FallDamage")
            107 -> tool.toggleSetting(player, "Global.FireDamage")
            108 -> tool.toggleSetting(player, "Global.DrowningDamage")
            109 -> tool.toggleSetting(player, "Global.FreezeDamage")
            110 -> tool.toggleSetting(player, "Global.Advancements")
            111 -> tool.toggleSetting(player, "Global.Deaths")
            112 -> tool.toggleSetting(player, "Global.KeepInv")
            113 -> tool.toggleSetting(player, "Global.Raids")
            114 -> tool.toggleSetting(player, "Global.FireTick")
            115 -> tool.toggleSetting(player, "Global.InstantRespawn")
            116 -> tool.toggleSetting(player, "Global.Phantoms")
            117 -> tool.toggleSetting(player, "Global.MobDamage")
            118 -> tool.toggleSetting(player, "Global.TickSpeed")
            119 -> tool.toggleSetting(player, "Global.F3")
        }
        val builder = GUIBuilder(player, GUI.WORLD_GLOBAL_SETTINGS)
        if (it.view.topInventory.size == 9*4) builder.scroll(0)
        else builder.storage(null)
        builder.open()
    }

    private fun main() {
        when (id) {
            200 -> {
                GUIBuilder(player, GUI.SELECT_MENU, GUIAnimation.SPLIT).custom().open()
                player.click()
            }
            1 -> {
                GUIBuilder(player, GUI.WORLD_OVERVIEW).storage(StorageFilters.NO_FILTER).open()
                player.click()
            }
            2 -> {
                player.closeInventory()
                player.playSound(player, Sound.ENTITY_ENDER_EYE_DEATH, 1f, 1f)
                WorldCreator(player)
            }
            3 -> {
                player.error()
                player.sendMessage("$prefix §cAktuell sind keine Daten vorhanden!")
            }
            4 -> {
                player.error()
                player.sendMessage("$prefix §cPer World Settings werden erst im nächsten Update verfügbar sein! Bis dahin kannst du globale Welteneinstellungen vornehmen")
            }
            5 -> {
                GUIBuilder(player, GUI.WORLD_GLOBAL_SETTINGS).scroll(0).open()
                player.click()
            }
        }
    }

    private fun overview() {
        when (id) {
            200 -> {
                GUIBuilder(player, GUI.WORLD_MAIN).custom().open()
                player.click()
            }
            else -> {
                id ?: return
                val world = if (id >= worlds.size) return
                else worlds[id]
                when (it.click) {
                    ClickType.LEFT -> {
                        player.teleport(world.spawnLocation)
                        player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                    }
                    ClickType.SHIFT_RIGHT -> {
                        if (id <= 2) {
                            player.sendMessage(msg("modules.world.defaultWorld"))
                            player.error()
                            return
                        }
                        val wTools = WorldTools()
                        wTools.deleteWorld(world)
                        player.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1f, 1.2f)
                        player.sendMessage(msg("modules.world.delete", player, world.name))
                        GUIBuilder(player, GUI.WORLD_OVERVIEW).storage(null).open()
                    }
                    else -> return
                }
            }
        }
    }
}