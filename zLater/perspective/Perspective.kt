package de.miraculixx.mutils.module.perspective

import de.miraculixx.kpaper.event.listen
import de.miraculixx.mutils.messages.*
import io.papermc.paper.event.player.AsyncChatEvent
import net.minecraft.network.protocol.game.*
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.event.vehicle.VehicleUpdateEvent

class Perspective {
    private val packets = PacketManagement()

    val onConnect = listen<PlayerJoinEvent> {
        packets.injectPlayer(it.player)
    }

    val onF = listen<PlayerSwapHandItemsEvent> {
        it.player.getNearbyEntities(2.0, 2.0, 2.0).forEach { e ->
            e.addPassenger(it.player)
        }
    }

    val onChat = listen<AsyncChatEvent> {
        val player = it.player
        val msg = plainSerializer.serialize(it.message())
        when (msg) {
            "create" -> packets.createNPC(player)
            "spawn" -> packets.spawn(player)
            "despawn" -> packets.despawn(player)
            "start" -> packets.startMoving(player)
            "stop" -> packets.stopMoving(player)
            "cam" -> it.player.getTargetEntity(6, true)?.let { it1 -> packets.setCamera(it1, it.player.name) }

            else -> return@listen
        }
        player.sendMessage(prefix + cmp("NPC $msg"))
    }

    val onMove = listen<PlayerMoveEvent> {
        //packets.teleport(it.player)
    }
}