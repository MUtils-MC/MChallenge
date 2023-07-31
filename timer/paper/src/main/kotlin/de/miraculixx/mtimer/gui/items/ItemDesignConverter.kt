package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mvanilla.extensions.lore
import de.miraculixx.mvanilla.extensions.name
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemDesignConverter(private val timerReal: PaperTimer, private val timerFake: PaperTimer) {
    fun getItem(design: TimerDesign, uuid: UUID): ItemStack {
        return itemStack(Material.NAME_TAG) {
            meta {
                val uuidString = uuid.toString()
                timerFake.design = design
                timerReal.design = design
                name = cmp(design.name, cHighlight)
                lore(
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
                    )
                )
                persistentDataContainer.set(NamespacedKey(namespace, "gui.timer.design"), PersistentDataType.STRING, uuidString)
                customModel = 10
            }
        }
    }
}