package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.items.skullTexture
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundDelete
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class CreatorDelete(val it: InventoryClickEvent) {

    init {
        event()
    }

    private fun event() {
        val item = it.currentItem
        val player = it.whoClicked as Player
        val top = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                customModel = 0
                name = cmp("Delete Challenges", cHighlight)
                itemMeta = skullTexture(
                    this,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
                )
            }
        }
        when (val id = item?.itemMeta?.customModel ?: 0) {
            200 -> {
                GUIBuilder(player, GUI.CREATOR_MAIN, GUIAnimation.SPLIT).custom().open()
                player.click()
            }

            else -> {
                if (!it.click.isShiftClick) return
                val challenge = CreatorManager.getChallenge(id - 1) ?: return
                CreatorManager.removeChallenge(challenge)
                player.soundDelete()
                GUIBuilder(player, GUI.CREATOR_DELETE).storage(null, getAllItems(), top).open()
            }
        }
    }

    private fun getAllItems(): Map<ItemStack, Boolean> {
        val tools = CreatorInvTools()
        return tools.getAllChallengeItems(cmp("Sneak click", cHighlight) + cmp(" ≫ Delete (PERMANENT)"))
    }
}