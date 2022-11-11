package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.challenges.ChDamager
import de.miraculixx.mutils.enums.modules.challenges.RivalCollectMode
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.modules.challenge.mods.noSameItems.NoSameItemEnum
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.tools.click
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class SettingsGUI(private val e: InventoryClickEvent, private val p: Player, private val title: String) {
    init {
        event()
    }

    fun event() {
        val menu = when (title) {
            GUI.SETTINGS_IN_TIME.title -> GUI.SETTINGS_IN_TIME
            GUI.SETTINGS_CAPTIVE.title -> GUI.SETTINGS_CAPTIVE
            GUI.SETTINGS_GHOST.title -> GUI.SETTINGS_GHOST
            GUI.SETTINGS_NO_SAME_ITEM.title -> GUI.SETTINGS_NO_SAME_ITEM
            GUI.SETTINGS_BOOST_UP.title -> GUI.SETTINGS_BOOST_UP
            GUI.SETTINGS_CHUNK_BREAKER.title -> GUI.SETTINGS_CHUNK_BREAKER
            GUI.SETTINGS_DAMAGER.title -> GUI.SETTINGS_DAMAGER
            GUI.SETTINGS_RIVAL_COLLECT.title -> GUI.SETTINGS_RIVAL_COLLECT
            else -> return
        }

        val c = ConfigManager.getConfig(Configs.MODULES)
        val item = e.currentItem
        val click = e.click

        val page = (e.inventory.getItem(22)?.amount ?: 1) - 1

        when (item?.itemMeta?.customModelData) {
            200 -> {
                GUIBuilder(p, GUI.CHALLENGE).scroll(0).open()
                p.click()
                return
            }
            300 -> InvUtils.numberChanger(c, p, click, "IN_TIME.PlayerTime", 10, 10, 500)
            301 -> InvUtils.numberChanger(c, p, click, "IN_TIME.MobTime", 10, 10, 500)
            302 -> InvUtils.numberChanger(c, p, click, "IN_TIME.DamageTime", 1, 1)
            303 -> InvUtils.numberChanger(c, p, click, "CAPTIVE.Size", 1, 1)
            304 -> InvUtils.numberChanger(c, p, click, "CAPTIVE.Amplifier", 1, 1)
            305 -> InvUtils.toggleSetting(c, p, "CAPTIVE.LevelMode")
            306 -> InvUtils.numberChanger(c, p, click, "GHOST.Radius", 1, 1)
            307 -> InvUtils.toggleSetting(c, p, "GHOST.Adventure")
            308 -> InvUtils.toggleSetting(c, p, "GHOST.Mode")
            309 -> InvUtils.numberChanger(c, p, click, "NO_SAME_ITEM.Lives", 1, 1, 20)
            310 -> InvUtils.toggleSetting(c, p, "NO_SAME_ITEM.SyncHeart")
            311 -> {
                val key = "NO_SAME_ITEM.Info"
                c[key] = when (NoSameItemEnum.valueOf(c.getString(key) ?: "EVERYTHING")) {
                    NoSameItemEnum.EVERYTHING -> NoSameItemEnum.ONLY_LIVES.name
                    NoSameItemEnum.ONLY_LIVES -> NoSameItemEnum.NOTHING.name
                    NoSameItemEnum.NOTHING -> NoSameItemEnum.EVERYTHING.name
                }
                p.click()
            }
            312 -> InvUtils.numberChanger(c, p, click, "BOOST_UP.Boost", 1, 1, 10)
            313 -> InvUtils.numberChanger(c, p, click, "BOOST_UP.Radius", 1, 1, 50)
            314 -> InvUtils.toggleSetting(c, p, "BOOST_UP.Mode")
            315 -> InvUtils.toggleSetting(c, p, "CHUNK_BLOCK_BREAK.Bundle")
            316 -> {
                c.set("DAMAGER.Mode", InvUtils.enumRotate(ChDamager.values(), ChDamager.valueOf(c.getString("DAMAGER.Mode") ?: "SLOT_CHANGE")).name)
                p.click()
            }
            317 -> InvUtils.numberChanger(c, p, click, "DAMAGER.Damage", 1, 1)
            318 -> {
                c.set("RIVALS_COLLECT.Mode", InvUtils.enumRotate(RivalCollectMode.values(), RivalCollectMode.valueOf(c.getString("RIVALS_COLLECT.Mode") ?: "ITEMS")).name)
                p.click()
            }
            319 -> InvUtils.numberChanger(c, p, click, "RIVALS_COLLECT.Joker", 1, 0)
        }
        GUIBuilder(p, menu).settings(page).open()
    }
}