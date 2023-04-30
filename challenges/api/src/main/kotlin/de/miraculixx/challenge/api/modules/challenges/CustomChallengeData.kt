package de.miraculixx.challenge.api.modules.challenges

import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.utils.Icon
import java.util.*

data class CustomChallengeData(
    val key: UUID,
    val challenge: Challenge,
    val data: ChallengeData,
    val icon: Icon,
    val tags: Set<ChallengeTags>,
)