package de.miraculixx.mutils.data

import de.miraculixx.mutils.extensions.UUIDExtension
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Duration

@Serializable
data class TimerData(
    val timerDesign: @Serializable(with = UUIDExtension::class) UUID,
    val time: Duration,
    val isVisible: Boolean,
    val countingUp: Boolean,
    val playerUUID: @Serializable(with = UUIDExtension::class) UUID? = null
)

@Serializable
data class TimerDesign(
    val running: TimerDesignPart,
    val idle: TimerDesignPart,
    var name: String,
    var owner: String
)

@Serializable
data class TimerDesignPart(
    var syntax: String,
    val days: TimerDesignValue,
    val hours: TimerDesignValue,
    val minutes: TimerDesignValue,
    val seconds: TimerDesignValue,
    val millis: TimerDesignValue,
    var prefix: String,
    var suffix: String,
    var animationSpeed: Float
)

@Serializable
data class TimerDesignValue(
    var forcedTwoDigits: Boolean,
    var visibleOnNull: Boolean,
    var prefix: String,
    var suffix: String
)