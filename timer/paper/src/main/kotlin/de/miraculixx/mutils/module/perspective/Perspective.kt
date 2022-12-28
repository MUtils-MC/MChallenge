package de.miraculixx.mutils.module.perspective

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.game.*
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class Perspective {
    val onConnect = listen<PlayerJoinEvent> {
        injectPlayer(it.player)
    }

    val onF = listen<PlayerSwapHandItemsEvent> {
        it.player.getNearbyEntities(2.0,2.0,2.0).forEach {e ->
            e.addPassenger(it.player)
        }
    }

    private fun removePlayer(player: Player) {
        val channel = (player as CraftPlayer).handle.connection.connection.channel
        channel.eventLoop().submit {
            channel.pipeline().remove(player.name)
        }
        console.sendMessage(prefix + cmp("Removed Player ${player.name} from packet stream"))
    }

    private fun injectPlayer(player: Player) {
        val channelDuplexHandler = PlayerChannelDuplexHandler()

        val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
        pipeline.addBefore("packet_handler", player.name, channelDuplexHandler)
        console.sendMessage(prefix + cmp("Injected Player ${player.name} to packet stream"))
    }

    class PlayerChannelDuplexHandler : ChannelDuplexHandler() {
        // Client to Server
        override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
            when (packet) {
                is ServerboundPlayerInputPacket -> {
                    console.sendMessage(cmp("(R-S) Player Input -> ${packet.xxa} ${packet.zza} ${packet.isJumping}"))
                }
            }
            super.channelRead(ctx, packet)
        }

        // Server to Client
        override fun write(ctx: ChannelHandlerContext?, packet: Any?, promise: ChannelPromise?) {
            super.write(ctx, packet, promise)
        }
    }
}