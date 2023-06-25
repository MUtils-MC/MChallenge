package de.miraculixx.mchallenge.modules.global

import de.miraculixx.kpaper.event.listen
import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.resources.ResourceLocation
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.*

@Serializable
data class ServerFeatureConfig(
    val teamTablistView: Boolean = false,
    val allowBlockHit: Boolean = false
)

@Serializable
data class BlockHitStatus(
    val blockHitting: Boolean,
    val uuid: String
)

class BlockHitManager : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (channel == "noriskclient:blockhit") {
            val payload = Payload(FriendlyByteBuf(Unpooled.copiedBuffer(message)).readUtf().toByteArray()).readJson<BlockHitStatus>()
            Bukkit.getServer().onlinePlayers.forEach {
                val buf = FriendlyByteBuf(Unpooled.buffer()).writeUtf(Payload().writeJson(payload).finishWrite())
                (it.player as CraftPlayer).handle.connection.send(ClientboundCustomPayloadPacket(ResourceLocation("noriskclient:blockhit"), buf))
            }
        }
    }

    init {
        listen<PlayerJoinEvent> {
            println("Sending to player")
            val buf = FriendlyByteBuf(Unpooled.buffer()).writeUtf(Payload().writeJson(ServerFeatureConfig(allowBlockHit = true)).finishWrite())
            (it.player as CraftPlayer).handle.connection.send(ClientboundCustomPayloadPacket(ResourceLocation("noriskclient:featureconfig"), buf))
            it.player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = 100.0
        }
    }
}

@Suppress("unused")
open class Payload(private var bytes: ByteArray = "".toByteArray()) : PayloadReader, PayloadWriter {

    private val decodedBytes: String
        get() = bytes.decodeToString()

    override fun readString(): String {
        val size = decodedBytes.split(" ")[0].toInt()
        bytes = decodedBytes.removePrefix("$size ").toByteArray()
        val string = decodedBytes.substring(0, size)
        bytes = decodedBytes.removePrefix("$string ").toByteArray()
        return string
    }

    override fun writeString(string: String): PayloadWriter {
        bytes = "${bytes.decodeToString()}${string.length} $string ".toByteArray()
        return this
    }

    override fun finishWrite(): String {
        val finishedPayload = bytes.decodeToString()
        bytes = "".toByteArray()
        return finishedPayload
    }

    fun dumpBytes(): ByteArray {
        return bytes
    }

    override fun toString(): String {
        return finishWrite()
    }

    fun copy(): Payload {
        return Payload(bytes.copyOf())
    }

    override fun copyWriter(): PayloadWriter {
        return copy()
    }

    override fun copyReader(): PayloadReader {
        return copy()
    }
}

interface PayloadReader {
    fun readString(): String

    fun readInt(): Int {
        return readString().toInt()
    }

    fun readDouble(): Double {
        return readString().toDouble()
    }

    fun readFloat(): Float {
        return readString().toFloat()
    }

    fun readShort(): Short {
        return readString().toShort()
    }

    fun readLong(): Long {
        return readString().toLong()
    }

    fun readUtf8Bytes(): ByteArray {
        return readString().toByteArray()
    }

    fun readBase64Bytes(): ByteArray {
        throw RuntimeException("Not implemented")
    }

    fun readBoolean(): Boolean {
        return readString().toBoolean()
    }

    fun readUUID(): UUID {
        return UUID.fromString(readString())
    }

    fun readDate(): Date {
        return Date(readLong())
    }

    fun copyReader(): PayloadReader
}

interface PayloadWriter {
    fun writeString(string: String): PayloadWriter

    fun writeInt(int: Int): PayloadWriter {
        return writeString(int.toString())
    }

    fun writeDouble(double: Double): PayloadWriter {
        return writeString(double.toString())
    }

    fun writeFloat(float: Float): PayloadWriter {
        return writeString(float.toString())
    }

    fun writeShort(short: Short): PayloadWriter {
        return writeString(short.toString())
    }

    fun writeLong(long: Long): PayloadWriter {
        return writeString(long.toString())
    }

    fun writeUtf8Bytes(bytes: ByteArray): PayloadWriter {
        return writeString(bytes.decodeToString())
    }

    fun writeBase64Bytes(bytes: ByteArray): PayloadWriter {
        throw RuntimeException("Not implemented")
    }

    fun writeBoolean(boolean: Boolean): PayloadWriter {
        return writeString(boolean.toString())
    }

    fun writeUUID(uuid: UUID): PayloadWriter {
        return writeString(uuid.toString())
    }

    fun writeDate(date: Date): PayloadWriter {
        return writeLong(date.time)
    }

    fun finishWrite(): String

    fun copyWriter(): PayloadWriter
}

inline fun <reified J> PayloadReader.readJson(): J {
    return Json.decodeFromString(readString())
}

inline fun <reified J> PayloadWriter.writeJson(json: J): PayloadWriter {
    return writeString(Json.encodeToString(json))
}