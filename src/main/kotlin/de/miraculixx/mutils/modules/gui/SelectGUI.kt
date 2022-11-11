package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.text.msg
import de.miraculixx.mutils.utils.tools.click
import net.axay.kspigot.chat.literalText
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class SelectGUI(private val e: InventoryClickEvent, private val p: Player) {
    init {
        event()
    }

    fun event() {
        val item = e.currentItem

        when (item?.itemMeta?.customModelData) {
            1 -> { //Challenges
                GUIBuilder(p, GUI.CHALLENGE, GUIAnimation.SPLIT).scroll(0).open()
                p.click()
            }
            2 -> { //World Manager
                GUIBuilder(p, GUI.WORLD_MAIN, GUIAnimation.SPLIT).custom().open()
                p.click()
            }
            3 -> { //Competitions
                if (!GUIListener.verify(p)) return
                p.sendMessage(msg("modules.global.soon", p, "Competitions"))
                p.click()
            }
            4 -> { //Timer
                GUIBuilder(p, GUI.TIMER_SETTINGS, GUIAnimation.SPLIT).custom().open()
                p.click()
            }
            5 -> { //Challenge Creator
                if (!GUIListener.verify(p)) return
                GUIBuilder(p, GUI.CREATOR_MAIN, GUIAnimation.SPLIT).custom().open()
                p.click()
            }
            6 -> { //Global Settings
                GUIBuilder(p, GUI.SERVER_SETTINGS, GUIAnimation.SPLIT).custom().open()
                p.click()
            }
            7 -> { //Speedrun
                if (!GUIListener.verify(p)) return
                if (challenges != ChallengeStatus.STOPPED) {
                    p.sendMessage(msg("modules.global.challengeActive", p))
                    p.playSound(p.location,Sound.ENTITY_ENDERMAN_TELEPORT,1f,1f)
                    return
                }
                p.closeInventory()
                p.playSound(p.location,Sound.ENTITY_ENDER_DRAGON_GROWL,1f,1.2f)
                //TODO()
                p.sendMessage(literalText(msg("modules.speedrun.warning", p)) {
                    onClickCommand("/speedrun confirm")
                })
            }
        }
    }
}