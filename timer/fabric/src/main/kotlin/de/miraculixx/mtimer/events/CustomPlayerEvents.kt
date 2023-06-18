package de.miraculixx.mtimer.events

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.silkmc.silk.core.event.Event
import net.silkmc.silk.core.event.EventScope
import net.silkmc.silk.core.event.Events

@Suppress("UnusedReceiverParameter") // receiver is for namespacing only
val Events.CustomPlayer get() = CustomPlayerEvents

object CustomPlayerEvents {
    open class PlayerBlockEvent(val player: Player?, val blockPos: BlockPos, val level: Level, var isCancelled: Boolean)

    open class PlayerItemEvent<T : Player?>(val player: T, val item: ItemStack, var isCancelled: Boolean)

    open class PlayerDamageEvent<T : Player?>(val player: T, val damageSource: DamageSource, val damage: Float, var isCancelled: Boolean)

    open class PlayerHungerChangeEvent(val player: Player?, val amount: Int, var isCancelled: Boolean)

    open class PlayerChatEvent(val player: ServerPlayer, val message: PlayerChatMessage, val bound: ChatType.Bound, var isCancelled: Boolean)

    val preBlockBreak = Event.onlySync<PlayerBlockEvent, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val preBlockPlace = Event.onlySync<PlayerBlockEvent, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val preDrop = Event.onlySync<PlayerItemEvent<Player>, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val onFinalDamage = Event.onlySync<PlayerDamageEvent<Player>, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val preHungerChange = Event.onlySync<PlayerHungerChangeEvent, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val preUseItem = Event.onlySync<PlayerItemEvent<Player>, EventScope.Cancellable> {
        EventScope.Cancellable()
    }

    val preSendChatMessage = Event.onlySync<PlayerChatEvent, EventScope.Cancellable> {
        EventScope.Cancellable()
    }
}