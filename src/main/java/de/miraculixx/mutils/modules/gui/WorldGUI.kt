package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.timer.Weather
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.modules.worldManager.WorldCreator
import de.miraculixx.mutils.modules.worldManager.WorldTools
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.gui.items.ItemLib
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.msg
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundError
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
        val click = it.click
        when (id) {
            200 -> {
                GUIBuilder(player, GUI.WORLD_MAIN, GUIAnimation.SPLIT).custom().open()
                player.click()
                return
            }
            205 -> {
                player.sendMessage("$prefix§c No Filter available!")
                player.soundError()
            }
            203 -> {
                if (click.isShiftClick)
                    InvUtils.navigate(player, 5, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                else InvUtils.navigate(player, 1, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                return
            }
            202 -> {
                if (click.isShiftClick)
                    InvUtils.navigate(player, -5, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
                else InvUtils.navigate(player, -1, GUI.WORLD_GLOBAL_SETTINGS, GUIState.SCROLL)
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
            } else InvUtils.toggleSetting(c, player, "Global.Backpack")
            101 -> InvUtils.toggleSetting(c, player, "Global.HeartsTab")
            102 -> InvUtils.toggleSetting(c, player, "Global.Hardcore")
            103 -> if (click.isRightClick) {
                InvUtils.enumRotate(Difficulty.values(), Difficulty.valueOf(c.getString("Global.DifficultyMode") ?: "EASY"))
                player.click()
            } else InvUtils.toggleSetting(c, player, "Global.Difficulty")
            104 -> InvUtils.toggleSetting(c, player, "Global.TimeFreeze")
            105 -> if (click.isRightClick) {
                InvUtils.enumRotate(Weather.values(), Weather.valueOf(c.getString("Global.WeatherMode") ?: "SUNNY"))
                player.click()
            } else InvUtils.toggleSetting(c, player, "Global.Weather")
            106 -> InvUtils.toggleSetting(c, player, "Global.FallDamage")
            107 -> InvUtils.toggleSetting(c, player, "Global.FireDamage")
            108 -> InvUtils.toggleSetting(c, player, "Global.DrowningDamage")
            109 -> InvUtils.toggleSetting(c, player, "Global.FreezeDamage")
            110 -> InvUtils.toggleSetting(c, player, "Global.Advancements")
            111 -> InvUtils.toggleSetting(c, player, "Global.Deaths")
            112 -> InvUtils.toggleSetting(c, player, "Global.KeepInv")
            113 -> InvUtils.toggleSetting(c, player, "Global.Raids")
            114 -> InvUtils.toggleSetting(c, player, "Global.FireTick")
            115 -> InvUtils.toggleSetting(c, player, "Global.InstantRespawn")
            116 -> InvUtils.toggleSetting(c, player, "Global.Phantoms")
            117 -> InvUtils.toggleSetting(c, player, "Global.MobDamage")
            118 -> InvUtils.toggleSetting(c, player, "Global.TickSpeed")
            119 -> InvUtils.toggleSetting(c, player, "Global.F3")
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
                GUIBuilder(player, GUI.WORLD_OVERVIEW).storage(StorageFilter.NO_FILTER).open()
                player.click()
            }
            2 -> {
                player.closeInventory()
                player.playSound(player, Sound.ENTITY_ENDER_EYE_DEATH, 1f, 1f)
                WorldCreator(player)
            }
            3 -> {
                player.soundError()
                player.sendMessage("$prefix §cAktuell sind keine Daten vorhanden!")
            }
            4 -> {
                player.soundError()
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
                            player.soundError()
                            return
                        }
                        val wInvUtils = WorldTools()
                        wInvUtils.deleteWorld(world)
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