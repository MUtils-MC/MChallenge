package de.miraculixx.mutils.modules.creator.data

import de.miraculixx.mutils.modules.creator.enums.CreatorAction
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeData(
    var name: String,
    var description: String,
    var icon: String,
    var author: String,
    var version: Versions,
    var events: List<Event>
)

@Serializable
data class Versions(val mc: String, val mutils: String)

@Serializable
data class Event(val event: CreatorEvent, val data: EventData)

@Serializable
data class EventData(val active: Boolean, val actions: Map<CreatorAction, List<String>>)
