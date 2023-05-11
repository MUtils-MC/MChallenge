package de.miraculixx.mtimer.gui.content

import de.miraculixx.mtimer.module.FabricTimer
import de.miraculixx.mtimer.server
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.data.ItemProvider
import de.miraculixx.mutils.gui.utils.getLore
import de.miraculixx.mutils.gui.utils.native
import de.miraculixx.mvanilla.messages.*
import net.minecraft.world.item.ItemStack
import net.silkmc.silk.core.item.setLore
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ItemsDesigns(private val timer: Timer) : ItemProvider {
    private val timerFake = FabricTimer(true, null, null, false, server.playerList)
    private val timerReal = FabricTimer(true, null, null, false, server.playerList)

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            timerFake.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
            timerReal.time = timer.time
            val converter = ItemDesignConverter(timerReal, timerFake)
            val loreAddon = listOf(
                msgClickLeft + cmp("Switch"),
                msgClickRight + cmp("Edit"),
                msgShiftClickRight + cmp("Delete")
            )
            TimerManager.getDesigns().forEach { (uuid, design) ->
                put(
                    converter.getItem(design, uuid).apply { setLore(getLore().plus(loreAddon.native())) },
                    timer.design == design
                )
            }
        }
    }
}