package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.utils.consoleWarn
import de.miraculixx.mutils.utils.plainSerializer
import de.miraculixx.mutils.utils.text.emptyComponent
import de.miraculixx.mutils.utils.tools.AwaitChatMessage
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import de.miraculixx.mutils.utils.tools.gui.items.ItemLib
import net.axay.kspigot.items.customModel
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class CreatorEditor(val it: InventoryClickEvent) {
    init {
        event()
    }

    private fun event() {
        val player = it.whoClicked as Player
        val item = it.currentItem
        val inventory = it.view.topInventory
        val rawID = plainSerializer.serialize(inventory.getItem(0)?.displayName() ?: emptyComponent()).removePrefix("[").removeSuffix("]")
        val uuid = try {
            UUID.fromString(rawID)
        } catch (_: IllegalArgumentException) {
            consoleWarn("Could not read Challenge Data! Error Code: 1 ($rawID)")
            return
        }
        val challenge = CreatorManager.getChallenge(uuid)
        if (challenge == null) {
            consoleWarn("Could not read Challenge Data! Error Code: 2")
            return
        }

        when (item?.itemMeta?.customModel ?: 0) {
            1 -> {
                awaitChat(player, challenge, "Challenge Name")
            }

            2 -> {
                awaitChat(player, challenge, "Challenge Description")
            }
        }
    }

    private fun awaitChat(player: Player, challengeData: CustomChallengeData, name: String) {
        val itemLib = ItemLib()
        player.closeInventory()
        AwaitChatMessage(false, player, name, 30, {
            val message = plainSerializer.serialize(it.message())
            if (name == "Challenge Name") challengeData.data.name = message
            else challengeData.data.description = message
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
            it.isCancelled = true
        }, {
            challengeData.update()
            GUIBuilder(player, GUI.CREATOR_MODIFY).custom(import = itemLib.getCreator(1, challengeData)).open()
        })
    }
}