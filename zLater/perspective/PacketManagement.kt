package de.miraculixx.mutils.module.perspective

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class PacketManagement {
    private val server = (Bukkit.getServer() as CraftServer).server
    private val connections = mutableListOf<ServerGamePacketListenerImpl>()
    private val npcs = mutableMapOf<String, PlayerNPC>() // String name to receive via packets

    fun getCurrentConnections(): List<ServerGamePacketListenerImpl> {
        return connections
    }

    fun removePlayer(player: Player) {
        val connection = (player as CraftPlayer).handle.connection
        val channel = connection.connection.channel
        channel.eventLoop().submit {
            channel.pipeline().remove(player.name)
        }
        console.sendMessage(prefix + cmp("Removed Player ${player.name} from packet stream"))
        connections.remove(connection)
    }

    fun injectPlayer(player: Player) {
        val channelDuplexHandler = PlayerChannelDuplexHandler(this)

        val connection = (player as CraftPlayer).handle.connection
        val pipeline = connection.connection.channel.pipeline()
        pipeline.addBefore("packet_handler", player.name, channelDuplexHandler)
        console.sendMessage(prefix + cmp("Injected Player ${player.name} to packet stream"))
        connections.add(connection)
    }

    fun createNPC(player: Player) {
        val nmsPlayer = (player as CraftPlayer).handle
        val npc = NPC(player.location)
        npcs[player.name] = PlayerNPC(nmsPlayer, npc, false)

        CoroutineScope(Dispatchers.Default).launch {
            npc.createNPC(server, player.location, player.uniqueId, player.name)
        }
    }

    fun spawn(player: Player) {
        val playerNPC = npcs[player.name] ?: return
        playerNPC.npc.spawn(connections)
    }

    fun despawn(player: Player) {
        val playerNPC = npcs[player.name] ?: return
        playerNPC.npc.despawn(connections)
    }

    fun startMoving(player: Player) {
        val playerNPC = npcs[player.name] ?: return
        playerNPC.isMoving = true
    }

    fun stopMoving(player: Player) {
        val playerNPC = npcs[player.name] ?: return
        playerNPC.isMoving = false
    }

    fun moveDirection(playerNPC: PlayerNPC, w: Boolean, a: Boolean, s: Boolean, d: Boolean, jump: Boolean, shift: Boolean) {
        if (!w && !a && !s && !d && !jump) return
        val npc = playerNPC.npc
        var z = 0.0
        var x = 0.0
        if (w) z+=1 // w -> z
        if (a) x+=1 // s -> -z
        if (s) z-=1 // a -> -x
        if (d) x-=1 // d -> x
        npc.move(Vec3(x, 0.0, z), connections)
    }

    fun moveHead(name: String) {
        val playerNPC = npcs[name] ?: return
        playerNPC.npc.headMove(playerNPC.player, connections)
    }

    fun setCamera(entity: Entity, name: String) {
        val playerNPC = npcs[name] ?: return
        val nmsEntity = (entity as CraftEntity).handle
        playerNPC.npc.setCamera(nmsEntity, connections)
    }

    fun teleport(player: Player) {
        val playerNPC = npcs[player.name] ?: return
        val nmsPlayer = (player as CraftEntity).handle
        playerNPC.npc.moveToPlayer(nmsPlayer, connections)
    }

    class PlayerChannelDuplexHandler(private val packetManager: PacketManagement) : ChannelDuplexHandler() {
        // Client to Server
        override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
            when (packet) {
                is ServerboundPlayerInputPacket -> {
                    //console.sendMessage(cmp("Player Input (${ctx.name()}) -> ${packet.xxa} ${packet.zza} ${packet.isJumping}"))
                    val playerNPC = packetManager.npcs[ctx.name()] ?: return
                    if (playerNPC.isMoving) {
                        packetManager.moveDirection(playerNPC, packet.zza > 0.5f, packet.xxa > 0.5f, packet.zza < -0.5f,
                            packet.xxa < -0.5f, packet.isJumping, packet.isShiftKeyDown)
                    }
                }

                is ServerboundMovePlayerPacket.Rot -> {
                    val playerNPC = packetManager.npcs[ctx.name()] ?: return
                    if (playerNPC.isMoving) {
                        packetManager.moveHead(ctx.name())
                    }
                }
            }
            super.channelRead(ctx, packet)
        }

        // Server to Client
        override fun write(ctx: ChannelHandlerContext?, packet: Any?, promise: ChannelPromise?) {
            super.write(ctx, packet, promise)
        }
    }

    data class PlayerNPC(val player: ServerPlayer, val npc: NPC, var isMoving: Boolean)
}