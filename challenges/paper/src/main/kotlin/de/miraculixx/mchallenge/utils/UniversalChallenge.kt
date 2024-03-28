package de.miraculixx.mchallenge.utils

import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mvanilla.data.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UniversalChallenge(
    val internal: Challenges? = null,
    val addon: @Serializable(with = UUIDSerializer::class) UUID? = null
)