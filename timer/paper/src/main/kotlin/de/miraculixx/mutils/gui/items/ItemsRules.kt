package de.miraculixx.mutils.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.rules
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsRules : ItemProvider {
    private val infoLore = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
    private val clickLore = listOf(emptyComponent(), msgClick + cmp("Toggle Rule"))

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            (from until to).forEach {
                val itemData = getItem(it)
                itemData.first?.let { item -> put(item, itemData.second) }
            }
        }
    }

    private fun getItem(id: Int): Pair<ItemStack?, Boolean> {
        return when (id) {
            0 -> Material.CLOCK.buildItem(6, "freezeWorld", rules.freezeWorld)
            1 -> Material.KNOWLEDGE_BOOK.buildItem(1, "announceSeed", rules.announceSeed)
            2 -> Material.MAP.buildItem(2, "announceLocation", rules.announceLocation)
            3 -> Material.WHITE_STAINED_GLASS.buildItem(3, "specOnDeath", rules.specOnDeath)
            4 -> Material.LIGHT_GRAY_STAINED_GLASS.buildItem(4, "specOnJoin", rules.specOnJoin)
            5 -> itemStack(Material.ANVIL) {
                meta {
                    customModel = 5
                    name = cmp(msgString("items.punishment.n"), cHighlight)
                    lore(
                        infoLore + msgList("items.punishment.l") + listOf(
                            emptyComponent(),
                            cmp("∙ ") + cmp("Settings", cHighlight, true),
                            cmp("   Action: ") + cmp(rules.punishmentSetting.type.name, cHighlight),
                            emptyComponent(),
                            msgClickLeft + cmp("Toggle Rule"),
                            msgClickRight + cmp("Switch Punishment")
                        )
                    )
                }
            } to rules.punishmentSetting.active
            6 -> Material.ENDER_PEARL.buildItem(7, "announceBack", rules.announceBack)
            7 -> Material.ENDER_EYE.buildItem(8, "syncWithChallenge", rules.syncWithChallenge)

            else -> null to false
        }
    }

    private fun Material.buildItem(id: Int, key: String, value: Boolean): Pair<ItemStack, Boolean> {
        return itemStack(this) {
            meta {
                customModel = id
                name = cmp(msgString("items.$key.n"), cHighlight)
                lore(infoLore + msgList("items.$key.l") + clickLore)
            }
        } to value
    }
}