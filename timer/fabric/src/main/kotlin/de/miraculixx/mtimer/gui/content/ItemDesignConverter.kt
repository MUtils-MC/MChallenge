package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.module.FabricTimer
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mutils.gui.utils.setLore
import de.miraculixx.mutils.gui.utils.setName
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.nbt.dsl.nbtCompound
import java.util.*

class ItemDesignConverter(private val timerReal: Timer, private val timerFake: FabricTimer, private val loreAddition: List<Component>) {
    fun getItem(design: TimerDesign, uuid: UUID): ItemStack {
        return itemStack(Items.NAME_TAG) {
            val uuidString = uuid.toString()
            timerFake.design = design
            timerReal.design = design
            setName(cmp(design.name, cHighlight))
            setLore(
                listOf(
                    cmp(uuidString, cHide),
                    cmp("Creator: ${design.owner}"),
                    emptyComponent(),
                    cmp("∙ ") + cmp("Design", cHighlight, underlined = true) + cmp(" (1d 10h 5m 20s)"),
                    cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timerFake.buildFormatted(true),
                    cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timerFake.buildFormatted(false),
                    emptyComponent(),
                    cmp("∙ ") + cmp("Design", cHighlight, underlined = true) + cmp(" (timer time)"),
                    cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timerReal.buildFormatted(true),
                    cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timerReal.buildFormatted(false),
                    emptyComponent()
                ).plus(loreAddition)
            )
            addTagElement(namespace, nbtCompound {
                put("timer-design", uuidString)
                put("ID", 10)
            })
        }
    }
}