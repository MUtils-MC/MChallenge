package de.miraculixx.challenge.api.modules.challenges

import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.utils.Icon
import java.util.*

/**
 * Represents a single challenge
 * @param key Unique mod UUID
 * @param challenge Mod instance (logic)
 * @param data All relevant data like settings
 * @param icon Item display in UI's
 * @param tags Tags for filtering
 * @param owner Your addon ID (visible on items)
 */
data class CustomChallengeData(
    val key: UUID,
    val challenge: Challenge,
    val data: ChallengeData,
    val icon: Icon,
    val tags: Set<ChallengeTags>,
    val owner: String
)