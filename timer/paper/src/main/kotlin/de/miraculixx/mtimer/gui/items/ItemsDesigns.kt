package de.miraculixx.mtimer.gui.items

import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mvanilla.extensions.native
import de.miraculixx.mvanilla.messages.*
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ItemsDesigns(private val timer: Timer) : ItemProvider {
    private val timerFake = PaperTimer(true, null, null, false)
    private val timerReal = PaperTimer(true, null, null, false)

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            timerFake.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
            timerReal.time = (timer.time)
            val converter = ItemDesignConverter(timerReal, timerFake)
            val loreAddon = listOf(
                msgClickLeft + cmp("Switch"),
                msgClickRight + cmp("Edit"),
                msgShiftClickRight + cmp("Delete")
            )
            TimerManager.getDesigns().forEach { (uuid, design) ->
                put(
                    converter.getItem(design, uuid).apply {
                        itemMeta = itemMeta?.apply { lore = (lore?.plus(loreAddon.native())) }
                    },
                    timer.design == design
                )
            }
        }
    }
}