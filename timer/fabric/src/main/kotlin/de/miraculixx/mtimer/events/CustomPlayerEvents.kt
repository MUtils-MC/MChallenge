package de.miraculixx.mtimer.events

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.silkmc.silk.core.event.*

@Suppress("UnusedReceiverParameter") // receiver is for namespacing only
val Events.CustomPlayer get() = CustomPlayerEvents

object CustomPlayerEvents {
    open class PlayerBlockEvent<T : Player?>(val player: T, val blockPos: BlockPos, val level: Level)

    open class PlayerItemEvent<T: Player?>(val player: T, val item: ItemStack)

    open class PlayerDamageEvent<T: Player?>(val player: T, val damageSource: DamageSource, val damage: Float)

    val preBlockBreak = Event.onlySync<PlayerBlockEvent<Player>, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val preDrop = Event.onlySync<PlayerItemEvent<Player>, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val onFinalDamage = Event.onlySync<PlayerDamageEvent<Player>, EventScope.Cancellable> {
        EventScope.Cancellable()
    }
}