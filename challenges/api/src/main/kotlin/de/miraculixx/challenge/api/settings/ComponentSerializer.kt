package de.miraculixx.challenge.api.settings

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

object ComponentSerializer : KSerializer<Component> {
    override val descriptor = PrimitiveSerialDescriptor("AdventureComponent", PrimitiveKind.STRING)
    private val componentSerializer = GsonComponentSerializer.gson()

    override fun deserialize(decoder: Decoder): Component {
        return componentSerializer.deserialize(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Component) {
        encoder.encodeString(componentSerializer.serialize(value))
    }
}