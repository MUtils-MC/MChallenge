package de.miraculixx.mutils.module.perspective

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.geometry.vec
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.mutils.api.WebClient
import de.miraculixx.mutils.messages.*
import kotlinx.serialization.decodeFromString
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.MoverType
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import java.util.*

class NPC(location: Location) {
    private val nmsWorld = (location.world as CraftWorld).handle
    private lateinit var npc: ServerPlayer
    private lateinit var signature: String
    private lateinit var texture: String

    suspend fun createNPC(server: DedicatedServer, location: Location, uuid: UUID, name: String) {
        val gameProfile = GameProfile(UUID.randomUUID(), name)
        val response = WebClient.getString("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
        val gameProfileData = json.decodeFromString<GameProfileData>(response)
        val skinData = gameProfileData.properties.firstOrNull()
        if (skinData == null) consoleAudience.sendMessage(prefix + cmp("Failed to resolve Skin data from player $name ($uuid)"))
        signature = skinData?.signature ?: ""
        texture = skinData?.value ?: ""
        gameProfile.properties.put("textures", Property("textures", texture, signature))
        npc = ServerPlayer(server, nmsWorld, gameProfile, null)
        npc.setPos(location.x, location.y, location.z)
    }

    fun spawn(connections: List<ServerGamePacketListenerImpl>) {
        val packetAdd = ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc)
        val packetInfo = ClientboundAddPlayerPacket(npc)

        val skinFixByte = (0x01 or 0x02 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40).toByte()
        val dataWatcher = npc.entityData
        dataWatcher.set(EntityDataAccessor(17, EntityDataSerializers.BYTE), skinFixByte)
        val packetSkinData = ClientboundSetEntityDataPacket(npc.id, dataWatcher, true)
        val packetTabRemover = ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc)

        connections.forEach { con ->
            con.send(packetAdd)
            con.send(packetInfo)
            con.send(packetSkinData)
            //con.send(packetTabRemover)
        }
    }

    fun despawn(connections: List<ServerGamePacketListenerImpl>) {
        val packet = ClientboundRemoveEntitiesPacket(npc.id)
        connections.forEach { it.send(packet) }
    }

    fun updateHolding(player: ServerPlayer, connections: List<ServerGamePacketListenerImpl>) {
        val connection = player.connection
        val inventory = player.inventory
        val mainHand = Pair(EquipmentSlot.MAINHAND, inventory.getSelected())
        val offHand = Pair(EquipmentSlot.OFFHAND, inventory.offhand.firstOrNull())
        val npcInv = npc.inventory
        npcInv.setItem(0, mainHand.second)
        npcInv.selected = 0
        // Offhand???

        val packet = ClientboundSetEquipmentPacket(npc.id, listOf(mainHand, offHand))
        connections.forEach { it.send(packet) }
    }

    fun updateArmor(player: ServerPlayer, connections: List<ServerGamePacketListenerImpl>) {
        val inventory = player.inventory
        val helmet = Pair(EquipmentSlot.HEAD, inventory.getArmor(3))
        val chest = Pair(EquipmentSlot.CHEST, inventory.getArmor(2))
        val legs = Pair(EquipmentSlot.LEGS, inventory.getArmor(1))
        val feet = Pair(EquipmentSlot.FEET, inventory.getArmor(0))
        val armor = npc.inventory.armor
        armor[3] = helmet.second
        armor[2] = chest.second
        armor[1] = legs.second
        armor[0] = feet.second

        val packet = ClientboundSetEquipmentPacket(npc.id, listOf(helmet, chest, legs, feet))
        connections.forEach { it.send(packet) }
    }

    fun move(vector: Vec3, connections: List<ServerGamePacketListenerImpl>) {
        sync {
            //val moveVec = getInputVector(vector, 0.2f, npc.xRot)
            npc.move(MoverType.SELF, vector.scale(0.2))
            //npc.moveRelative(0.2f, vector)
            console.sendMessage("$vector - ${npc.position().x} ${npc.position().z}")
            val packetLoc = ClientboundTeleportEntityPacket(npc)
            connections.forEach { it.send(packetLoc) }
        }
        //consoleAudience.sendMessage(cmp("To ${npc.position()}"))
    }

    fun headMove(player: ServerPlayer, connections: List<ServerGamePacketListenerImpl>) {
        val yaw = player.yRot
        val pitch = player.xRot
        val packetHeadRotation = ClientboundRotateHeadPacket(npc, (yaw * 256 / 360).toInt().toByte())
        val packetBodyRotation = ClientboundMoveEntityPacket.Rot(npc.id, (yaw * 256 / 360).toInt().toByte(), (pitch * 256 / 360).toInt().toByte(), false)
        connections.forEach { con ->
            con.send(packetHeadRotation)
            con.send(packetBodyRotation)
        }
    }

    fun toggleSneak(active: Boolean) {

    }

    fun setCamera(entity: Entity, connections: List<ServerGamePacketListenerImpl>) {
        val packet = ClientboundSetCameraPacket(entity)
        connections.forEach { it.send(packet) }
    }

    fun moveToPlayer(player: Entity, connections: List<ServerGamePacketListenerImpl>) {
        val loc = player.position()
        val yaw = player.yRot
        val pitch = player.xRot
        npc.moveTo(loc.x, loc.y, loc.z, yaw, pitch)
        val packetLoc = ClientboundTeleportEntityPacket(npc)
        val packetHead = ClientboundMoveEntityPacket.Rot(npc.id, (yaw * 256 / 360).toInt().toByte(), (pitch * 256 / 360).toInt().toByte(), false)
        connections.forEach { con ->
            con.send(packetLoc)
            con.send(packetHead)
        }
    }

    private fun calcShort(double: Double): Short {
        return double.toInt().times(1000).toShort()
    }

    private fun getInputVector(movementInput: Vec3, speed: Float, yaw: Float): Vec3 {
        val d0 = movementInput.lengthSqr()
        return if (d0 < 1.0E-7) {
            Vec3.ZERO
        } else {
            val vec3d1 = (if (d0 > 1.0) movementInput.normalize() else movementInput).scale(speed.toDouble())
            val f2 = Mth.sin(yaw * 0.017453292f)
            val f3 = Mth.cos(yaw * 0.017453292f)
            Vec3(vec3d1.x * f3.toDouble() - vec3d1.z * f2.toDouble(), vec3d1.y, vec3d1.z * f3.toDouble() + vec3d1.x * f2.toDouble())
        }
    }
}