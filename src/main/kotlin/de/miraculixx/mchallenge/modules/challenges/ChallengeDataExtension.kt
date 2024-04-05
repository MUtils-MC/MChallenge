package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.challenge.api.settings.ChallengeData

/**
 * @return All setting data for a default challenge
 */
fun MutableMap<Challenges, ChallengeData>.getSetting(challenge: Challenges): ChallengeData {
    return getOrPut(challenge) {
        ChallengeData(challenge.getDefaultSettings(), emptyMap(), false)
    }
}
