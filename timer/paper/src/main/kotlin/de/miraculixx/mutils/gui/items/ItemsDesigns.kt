package de.miraculixx.mutils.gui.items

import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.Timer
import de.miraculixx.mutils.module.TimerManager
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ItemsDesigns(private val timer: Timer): ItemProvider {
    private val timerFake = Timer(true, null, null, false)
    private val timerReal = Timer(true, null, null, false)

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            timerFake.setTime(1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
            timerReal.setTime(timer.getTime())
            val converter = ItemDesignConverter(timerReal, timerFake)
            val loreAddon = listOf(
                msgClickLeft + cmp("Switch"),
                msgClickRight + cmp("Edit"),
                msgShiftClickRight + cmp("Delete")
            )
            TimerManager.getDesigns().forEach { (uuid, design) ->
                put(converter.getItem(design, uuid).apply { lore(lore()?.plus(loreAddon)) },
                    timer.design == design)
            }
        }
    }
}