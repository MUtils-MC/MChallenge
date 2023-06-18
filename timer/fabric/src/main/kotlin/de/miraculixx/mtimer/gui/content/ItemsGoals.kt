package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.vanilla.module.goals
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.messages.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.nbt.dsl.nbtCompound

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
            0 -> itemStack(Items.DRAGON_EGG) {
                addTagElement(namespace, nbtCompound { put("ID", 1) })
                setName(cmp(msgString("items.dragonDeath.n"), cHighlight))
                setLore(infoLore + msgList("items.dragonDeath.l") + clickLore)

            } to goals.enderDragon

            1 -> itemStack(Items.WITHER_ROSE) {
                addTagElement(namespace, nbtCompound { put("ID", 2) })
                setName(cmp(msgString("items.witherDeath.n"), cHighlight))
                setLore(infoLore + msgList("items.witherDeath.l") + clickLore)
            } to goals.wither

            2 -> itemStack(Items.PRISMARINE_CRYSTALS) {
                addTagElement(namespace, nbtCompound { put("ID", 3) })
                setName(cmp(msgString("items.elderDeath.n"), cHighlight))
                setLore(infoLore + msgList("items.elderDeath.l") + clickLore)
            } to goals.elderGuardian

            3 -> itemStack(if (majorVersion >= 19) Items.ECHO_SHARD else Items.WARPED_HYPHAE) {
                addTagElement(namespace, nbtCompound { put("ID", 4) })
                setName(cmp(msgString("items.wardenDeath.n"), cHighlight))
                setLore(infoLore + msgList("items.wardenDeath.l") + clickLore)
            } to goals.warden

            4 -> itemStack(Items.PLAYER_HEAD) {
                addTagElement(namespace, nbtCompound { put("ID", 5) })
                setName(cmp(msgString("items.playerDeath.n"), cHighlight))
                setLore(infoLore + msgList("items.playerDeath.l") + clickLore)
            } to goals.playerDeath

            5 -> itemStack(Items.STRUCTURE_VOID) {
                addTagElement(namespace, nbtCompound { put("ID", 6) })
                setName(cmp(msgString("items.lastPlayer.n"), cHighlight))
                setLore(infoLore + msgList("items.lastPlayer.l") + clickLore)
            } to goals.emptyServer

            else -> null to false
        }
    }
}