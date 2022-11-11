package de.miraculixx.mutils.modules.creator.data

import de.miraculixx.mutils.modules.creator.enums.CreatorAction
import de.miraculixx.mutils.modules.creator.enums.CreatorActionInput
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.system.serializer.UUIDExtension
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ChallengeData(
    var name: String,
    var description: String,
    var icon: String,
    var author: Author,
    var version: Versions,
    var events: List<Event>
)

@Serializable
data class Versions(val mc: String, val mutils: String)

@Serializable
data class Author(val name: String, val uuid: @Serializable(with = UUIDExtension::class) UUID)

@Serializable
data class Event(val event: CreatorEvent, val data: EventData)

@Serializable
data class EventData(var active: Boolean, val actions: MutableMap<@Serializable(with = UUIDExtension::class) UUID, ActionData>)

@Serializable
data class ActionData(val uuid: @Serializable(with = UUIDExtension::class) UUID, val action: CreatorAction, val settings: MutableList<String>)

data class ActionValueData(val name: String, val type: CreatorActionInput)
