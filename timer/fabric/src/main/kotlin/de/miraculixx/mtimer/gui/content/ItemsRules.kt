package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.messages.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.nbt.dsl.nbtCompound

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
            0 -> Items.CLOCK.buildItem(6, "freezeWorld", rules.freezeWorld)
            1 -> Items.KNOWLEDGE_BOOK.buildItem(1, "announceSeed", rules.announceSeed)
            2 -> Items.MAP.buildItem(2, "announceLocation", rules.announceLocation)
            3 -> Items.WHITE_STAINED_GLASS.buildItem(3, "specOnDeath", rules.specOnDeath)
            4 -> Items.LIGHT_GRAY_STAINED_GLASS.buildItem(4, "specOnJoin", rules.specOnJoin)
            5 -> itemStack(Items.ANVIL) {
                addTagElement(namespace, nbtCompound { put("ID", 5) })
                setName(cmp(msgString("items.punishment.n"), cHighlight))
                setLore(
                    infoLore + msgList("items.punishment.l") + listOf(
                        emptyComponent(),
                        cmp("∙ ") + cmp("Settings", cHighlight, true),
                        cmp("   Action: ") + cmp(rules.punishmentSetting.type.name, cHighlight),
                        emptyComponent(),
                        msgClickLeft + cmp("Toggle Rule"),
                        msgClickRight + cmp("Switch Punishment")
                    )
                )
            } to rules.punishmentSetting.active

            6 -> Items.ENDER_PEARL.buildItem(7, "announceBack", rules.announceBack)
            7 -> Items.ENDER_EYE.buildItem(8, "syncWithChallenges", rules.syncWithChallenge)

            else -> null to false
        }
    }

    private fun ItemLike.buildItem(id: Int, key: String, value: Boolean): Pair<ItemStack, Boolean> {
        return itemStack(this) {
            addTagElement(namespace, nbtCompound { put("ID", id) })
            setName(cmp(msgString("items.$key.n"), cHighlight))
            setLore(infoLore + msgList("items.$key.l") + clickLore)
        } to value
    }
}