package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mtimer.vanilla.module.goals
import de.miraculixx.mvanilla.extensions.lore
import de.miraculixx.mvanilla.extensions.name
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsGoals : ItemProvider {
    private val infoLore = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
    private val clickLore = listOf(emptyComponent(), msgClick + cmp("Toggle Goal"))

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            (from..to).forEach {
                val itemData = getItem(it)
                itemData.first?.let { item -> put(item, itemData.second) }
            }
        }
    }

    private fun getItem(id: Int): Pair<ItemStack?, Boolean> {
        return when (id) {
            0 -> itemStack(Material.DRAGON_EGG) {
                meta {
                    customModel = 1
                    name = cmp(msgString("items.dragonDeath.n"), cHighlight)
                    lore(infoLore + msgList("items.dragonDeath.l") + clickLore)
                }
            } to goals.enderDragon

            1 -> itemStack(Material.WITHER_ROSE) {
                meta {
                    customModel = 2
                    name = cmp(msgString("items.witherDeath.n"), cHighlight)
                    lore(infoLore + msgList("items.witherDeath.l") + clickLore)
                }
            } to goals.wither

            2 -> itemStack(Material.PRISMARINE_CRYSTALS) {
                meta {
                    customModel = 3
                    name = cmp(msgString("items.elderDeath.n"), cHighlight)
                    lore(infoLore + msgList("items.elderDeath.l") + clickLore)
                }
            } to goals.elderGuardian

            3 -> itemStack(if (majorVersion >= 19) Material.ECHO_SHARD else Material.WARPED_HYPHAE) {
                meta {
                    customModel = 4
                    name = cmp(msgString("items.wardenDeath.n"), cHighlight)
                    lore(infoLore + msgList("items.wardenDeath.l") + clickLore)
                }
            } to goals.warden

            4 -> itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 5
                    name = cmp(msgString("items.playerDeath.n"), cHighlight)
                    lore(infoLore + msgList("items.playerDeath.l") + clickLore)
                }
            } to goals.playerDeath

            5 -> itemStack(Material.STRUCTURE_VOID) {
                meta {
                    customModel = 6
                    name = cmp(msgString("items.lastPlayer.n"), cHighlight)
                    lore(infoLore + msgList("items.lastPlayer.l") + clickLore)
                }
            } to goals.emptyServer

            else -> null to false
        }
    }
}