package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.tools.click
import net.axay.kspigot.items.customModel
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class ChallengeGUI(private val e: InventoryClickEvent, private val p: Player) {
    private val c = ConfigManager.getConfig(Configs.MODULES)
    
    init {
        event()
    }

    fun event() {
        val item = e.currentItem
        val cl = e.click

        val page = (e.inventory.getItem(31)?.amount ?: 1) - 1

        when (item?.itemMeta?.customModel ?: 0) {
            200 -> {
                GUIBuilder(p, GUI.SELECT_MENU, animation = GUIAnimation.SPLIT).custom().open()
                p.click()
                return
            }
            201 -> {
                GUIBuilder(p, GUI.CHALLENGE).storage(StorageFilter.NO_FILTER).open()
                p.click()
                return
            }
            202 -> {
                val change = if (cl.isShiftClick) -5
                else -1
                InvUtils.navigate(p, change, GUI.CHALLENGE, GUIState.SCROLL)
                return
            }
            203 -> {
                val change = if (cl.isShiftClick) 5
                else 1
                InvUtils.navigate(p, change, GUI.CHALLENGE, GUIState.SCROLL)
                return
            }
            205 -> {
                val lore = item?.lore
                val currentFilter = if ((lore?.size ?: 0) < 5) StorageFilter.NO_FILTER
                else StorageFilter.valueOf(lore?.get(4)?.removePrefix("§7∙ ")?.replace(' ', '_') ?: "NO_FILTER")
                val newFilter = InvUtils.enumRotate(
                    arrayOf(
                        StorageFilter.NO_FILTER, StorageFilter.FUN, StorageFilter.MEDIUM,
                        StorageFilter.HARD, StorageFilter.BETA, StorageFilter.COMPLEX, StorageFilter.FORCE, StorageFilter.RANDOMIZER,
                        StorageFilter.MULTIPLAYER, StorageFilter.VERSION_BOUND
                    ), currentFilter
                )
                GUIBuilder(p, GUI.CHALLENGE).storage(newFilter).open()
                p.click()
            }
            114 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.DAMAGE_DUELL, p)
                else InvUtils.numberChangerShift(c, p, cl, "DAMAGE_DUELL.Percent", 10, 10)
            }
            113 -> toggle(Modules.SPLIT_HP, p)
            112 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.RUN_RANDOMIZER, p)
                else InvUtils.numberChangerShift(c, p, cl, "RUN_RANDOMIZER.Goal", 50, 50, 10000)
            }
            100 -> {
                val key = "FLY.Boost"
                if (cl == ClickType.LEFT) {
                    toggle(Modules.FLY, p)
                } else InvUtils.numberChangerShift(c,p, cl, key, 1, 2, 10)
            }
            101 -> {
                if (cl == ClickType.LEFT) {
                    toggle(Modules.IN_TIME, p)
                } else if (cl == ClickType.RIGHT) {
                    GUIBuilder(p, GUI.SETTINGS_IN_TIME).settings(page).open()
                    p.click()
                    return
                }
            }
            102 -> {
                if (cl == ClickType.LEFT) {
                    toggle(Modules.MOB_RANDOMIZER, p)
                } else if (cl == ClickType.RIGHT)
                    InvUtils.toggleSetting(c, p, "MOB_RANDOMIZER.Random")
            }
            103 -> if (cl == ClickType.LEFT)
                toggle(Modules.CHECKPOINTS, p)
            else if (cl == ClickType.RIGHT)
                InvUtils.toggleSetting(c, p, "CHECKPOINTS.Teleport")
            104 -> {
                if (cl == ClickType.LEFT) toggle(Modules.DIM_SWAP, p)
                else if (cl == ClickType.RIGHT)
                    InvUtils.toggleSetting(c, p, "DIM_SWAP.Pickaxe")
            }
            105 -> {
                if (cl == ClickType.LEFT) {
                    toggle(Modules.SNAKE, p)
                } else InvUtils.numberChangerShift(c, p, cl, "SNAKE.Speed", 1, 1, 10)
            }
            106 -> toggle(Modules.REALISTIC, p)
            107 -> if (cl == ClickType.LEFT) {
                toggle(Modules.CAPTIVE, p)
            } else {
                GUIBuilder(p, GUI.SETTINGS_CAPTIVE).settings(page).open()
                p.click()
                return
            }
            108 -> if (cl == ClickType.LEFT) {
                toggle(Modules.GHOST, p)
            } else {
                GUIBuilder(p, GUI.SETTINGS_GHOST).settings(page).open()
                p.click()
                return
            }
            109 -> toggle(Modules.BLOCK_ASYNC, p)
            110 -> {
                if (cl == ClickType.LEFT) {
                    toggle(Modules.NO_SAME_ITEM, p)
                } else {
                    GUIBuilder(p, GUI.SETTINGS_NO_SAME_ITEM).settings(page).open()
                    p.click()
                    return
                }
            }
            111 -> {
                if (cl == ClickType.LEFT) {
                    toggle(Modules.LIMITED_SKILLS, p)
                } else InvUtils.toggleSetting(c, p, "LIMITED_SKILLS.Random")
            }
            115 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.ONE_BIOME, p)
                else InvUtils.numberChangerShift(c, p, cl, "ONE_BIOME.Delay", 10, 30, 1000)
            }
            116 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.BOOST_UP, p)
                else {
                    GUIBuilder(p, GUI.SETTINGS_BOOST_UP).settings(page).open()
                    p.click()
                    return
                }
            }
            117 -> toggle(Modules.RIGHT_TOOL, p)
            118 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.CHUNK_BLOCK_BREAK, p)
                else {
                    GUIBuilder(p, GUI.SETTINGS_CHUNK_BREAKER).settings(page).open()
                    p.click()
                    return
                }
            }
            119 -> {
                if (cl == ClickType.LEFT) toggle(Modules.SNEAK_SPAWN, p)
                else InvUtils.toggleSetting(c, p, "SNEAK_SPAWN.Mobs")
            }
            120 -> toggle(Modules.WORLD_PEACE, p)
            121 -> if (cl == ClickType.LEFT) toggle(Modules.GRAVITY, p)
            else InvUtils.numberChangerShift(c, p, cl, "GRAVITY.Delay", 10, 10, 9990)
            122 -> if (cl == ClickType.LEFT) toggle(Modules.STAY_AWAY, p)
            else InvUtils.numberChangerShift(c, p, cl, "STAY_AWAY.Distance", 0.5, 0.5, 20.0)
            123 -> if (cl == ClickType.LEFT) toggle(Modules.RANDOMIZER_BLOCK, p)
            else InvUtils.toggleSetting(c, p, "RANDOMIZER_BLOCK.Random")
            124 -> if (cl == ClickType.LEFT) toggle(Modules.RANDOMIZER_ENTITY, p)
            else InvUtils.toggleSetting(c, p, "RANDOMIZER_ENTITY.Random")
            125 -> if (cl == ClickType.LEFT) toggle(Modules.RANDOMIZER_BIOMES, p)
            else InvUtils.toggleSetting(c, p, "RANDOMIZER_BIOMES.Random")
            126 -> if (cl == ClickType.LEFT) toggle(Modules.RANDOMIZER_MOBS, p)
            else InvUtils.toggleSetting(c, p, "RANDOMIZER_MOBS.Random")
            127 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.FORCE_COLLECT, p)
                else {
                    GUIBuilder(p, GUI.SETTINGS_FORCE_COLLECT).settings(page).open()
                    p.click()
                    return
                }
            }
            128 -> if (cl == ClickType.LEFT) toggle(Modules.RANDOMIZER_ENTITY_DAMAGE, p)
            else InvUtils.toggleSetting(c, p, "RANDOMIZER_ENTITY_DAMAGE.Random")
            129 -> if (cl == ClickType.LEFT) toggle(Modules.NO_DOUBLE_KILL, p)
            else InvUtils.toggleSetting(c, p, "NO_DOUBLE_KILL.Global")
            130 -> {
                if (cl == ClickType.LEFT)
                    toggle(Modules.DAMAGER, p)
                else {
                    GUIBuilder(p, GUI.SETTINGS_DAMAGER).settings(page).open()
                    p.click()
                    return
                }
            }
            131 -> if (cl == ClickType.LEFT)
                toggle(Modules.RIVALS_COLLECT, p)
            else {
                GUIBuilder(p, GUI.SETTINGS_RIVAL_COLLECT).settings(page).open()
                p.click()
                return
            }
        }
        if (e.inventory.size == 9 * 6) GUIBuilder(p, GUI.CHALLENGE).storage(null).open()
        else GUIBuilder(p, GUI.CHALLENGE).scroll(0).open()
    }

    private fun toggle(m: Modules, player: Player) {
        if (!InvUtils.verify(m, player)) return
        if (ModuleManager.isActive(m)) {
            ModuleManager.disableModule(m)
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
        } else {
            if (ModuleManager.enableModule(m))
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        }
    }
}