package de.miraculixx.mchallenge.utils.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Vector2d

object Vector2dSerializer : KSerializer<Vector2d> {
    override val descriptor = PrimitiveSerialDescriptor("Vector2d", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Vector2d) {
        encoder.encodeString("${value.x}|${value.y}")
    }

    override fun deserialize(decoder: Decoder): Vector2d {
        val (x, y) = decoder.decodeString().split("|").map { it.toDouble() }
        return Vector2d(x, y)
    }
}