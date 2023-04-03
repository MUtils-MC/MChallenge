package de.miraculixx.mchallenge.modules

import de.miraculixx.api.MChallengeAPI
import de.miraculixx.api.modules.challenges.StatusChangerAPI
import de.miraculixx.mchallenge.modules.challenges.StatusChanger

object ChallengeManager : MChallengeAPI() {
    init {
        instance = this
    }
    override val statusChanger: StatusChangerAPI = StatusChanger()
}