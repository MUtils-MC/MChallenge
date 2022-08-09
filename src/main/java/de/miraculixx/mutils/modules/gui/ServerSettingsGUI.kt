package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.*
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.error
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.sync
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.io.File

class ServerSettingsGUI(private val e: InventoryClickEvent, private val p: Player, private val title: String) {
    init {
        event()
    }

    fun event() {
        val item = e.currentItem
        val conf = ConfigManager.getConfig(Configs.SETTINGS)
        val tools = GUITools(conf)
        val click = e.click

        when (item?.itemMeta?.customModelData) {
            200 -> {
                if (title.contains("∙"))
                    GUIBuilder(p, GUI.SERVER_SETTINGS).custom().open()
                else GUIBuilder(p, GUI.SELECT_MENU).custom().open()
                p.click()
                return
            }
            100 -> {
                p.closeInventory()
                GUITools.AwaitChat(conf, "Server Icon", p) {
                    val input = conf.getString("Server Icon") ?: "icon.png"
                    serverIcon = try {
                        Bukkit.loadServerIcon(File(input))
                    } catch (e: Exception) {
                        p.sendMessage(msg("modules.serverSettings.iconFailed", p, input))
                        p.error()
                        null
                    }
                    GUIBuilder(p, GUI.SERVER_SETTINGS).custom().open()
                }
                return
            }
            101 -> {
                p.closeInventory()
                GUITools.AwaitChat(conf, "MOTD", p) {
                    GUIBuilder(p, GUI.SERVER_SETTINGS).custom().open()
                }
                return
            }
            102 -> tools.numberChanger(p, click, "Slots", 1, 1)
            103 -> {
                GUIBuilder(p, GUI.BANNED_PLAYERS).storage(null).open()
                p.click()
                return
            }
            104 -> {
                if (click.isRightClick) {
                    Bukkit.setWhitelist(!Bukkit.hasWhitelist())
                    p.click()
                } else {
                    GUIBuilder(p, GUI.WHITELIST_PLAYERS).storage(null).open()
                    p.click()
                    return
                }
            }
            105 -> {
                CoroutineScope(Dispatchers.Default).launch {
                    if (ConfigManager.upload(Configs.MODULES, "modules")) {
                        p.sendMessage(msg("modules.serverSettings.upload"))
                    } else p.sendMessage(msg("modules.serverSettings.uploadFail"))
                    sync {
                        p.closeInventory()
                    }
                }
                return
            }
            106 -> {
                CoroutineScope(Dispatchers.Default).launch {
                    val verify = ConfigManager.getLicenceData()
                    val s = File.separator
                    val path = Main.INSTANCE.dataFolder.path + s
                    API.download("private/$ID-${verify.first}/modules.yml",path + "modules${s}modules.yml")
                    ConfigManager.reload(Configs.MODULES)
                    p.sendMessage(msg("modules.serverSettings.download"))
                    sync {
                        p.closeInventory()
                    }
                }
                return
            }

            150 -> {
                Bukkit.getBannedPlayers().remove(getPlayer(item))
                GUIBuilder(p, GUI.BANNED_PLAYERS).storage(null).open()
                p.click()
                return
            }
            151 -> {
                val player = getPlayer(item)
                player?.isWhitelisted = false
                GUIBuilder(p, GUI.WHITELIST_PLAYERS).storage(null).open()
                p.click()
                return
            }
        }
        GUIBuilder(p, GUI.SERVER_SETTINGS).custom().open()
    }

    private fun getPlayer(item: ItemStack): OfflinePlayer? {
        val player = Bukkit.getOfflinePlayer(item.itemMeta?.name?.cropColor() ?: "null")
        if (player.name == "null") {
            p.error()
            p.sendMessage("§cAn unexpected error occurred! Please report this error")
            return null
        }
        return player
    }
}
