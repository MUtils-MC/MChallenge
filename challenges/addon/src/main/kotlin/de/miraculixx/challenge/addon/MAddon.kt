package de.miraculixx.challenge.addon

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.CustomChallengeData
import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.settings.ChallengeIntSetting
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.kpaper.main.KSpigot
import net.kyori.adventure.text.Component
import java.util.*

class MAddon : KSpigot() {

    companion object {
        val challengeUUID = UUID.randomUUID()
        val challengeData = CustomChallengeData(
            challengeUUID,
            DamageOnChest(),
            ChallengeData(
                mapOf("damage" to ChallengeIntSetting("BEETROOT", 2, "hp", min = 1, max = 20)),
                mapOf("damage" to IconNaming(Component.text("Damage Amount"), listOf(Component.text("The amount of Damage on interaction")))),
            ),
            Icon("CHEST", naming = IconNaming(Component.text("Chest Damage"), listOf(Component.text("You get damage on chest"), Component.text("interactions")))),
            setOf(ChallengeTags.FREE)
        )
    }

    override fun load() {

    }

    override fun startup() {
        MChallengeAPI.instance?.addChallenge(challengeUUID, )
    }
}