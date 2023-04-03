package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsMenu : ItemProvider {
    private val infoLore = listOf(emptyComponent(), cmp("• ") + cmp("Info", cHighlight, underlined = true))
    private val clickLore = listOf(emptyComponent(), msgClick + cmp("Open Overview"))

    override fun getSlotMap(): Map<ItemStack, Int> {
        return mapOf(
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(msgString("items.menu.worldOverview.n"), cHighlight)
                    customModel = 1
                    lore(infoLore + msgList("items.menu.worldOverview.l") + clickLore)
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.GLOBE.value)
            } to 10,
            itemStack(Material.CRAFTING_TABLE) {
                meta {
                    name = cmp(msgString("items.menu.worldCreator.n"), cHighlight)
                    customModel = 2
                    lore(infoLore + msgList("items.menu.worldCreator.l") + clickLore)
                }
            } to 11,
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("items.menu.worldDatas.n"), cHighlight)
                    customModel = 3
                    lore(infoLore + msgList("items.menu.worldDatas.l") + clickLore)
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ENDER_CHEST.value)
            } to 13,
            itemStack(Material.GLOWSTONE_DUST) {
                meta {
                    name = cmp(msgString("items.menu.worldSettings.n"), cHighlight)
                    customModel = 4
                    lore(infoLore + msgList("items.menu.worldSettings.l") + clickLore)
                }
            } to 15,
            itemStack(Material.REDSTONE) {
                meta {
                    name = cmp(msgString("items.menu.worldSettingsGlobal.n"), cHighlight)
                    customModel = 5
                    lore(infoLore + msgList("items.menu.worldSettingsGlobal.l") + clickLore)
                }
            } to 16,
        )
    }
}