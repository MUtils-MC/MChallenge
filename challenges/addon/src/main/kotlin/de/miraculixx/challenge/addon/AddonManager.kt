package de.miraculixx.challenge.addon

import de.miraculixx.challenge.addon.utils.UUIDSerializer
import de.miraculixx.challenge.addon.utils.cmp
import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.CustomChallengeData
import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.settings.ChallengeIntSetting
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.kpaper.extensions.console
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.format.NamedTextColor
import java.io.File
import java.util.*

object AddonManager {
    private val configFile = File("${PluginInstance.dataFolder.path}/settings.json")
    private val challengeUUID: UUID = UUID.randomUUID()
    private val settingsDefaults = mapOf(
        challengeUUID to ChallengeData(
            mapOf("damage" to ChallengeIntSetting("BEETROOT", 2, "hp", min = 1, max = 20)),
            mapOf("damage" to IconNaming(cmp("Damage Amount"), listOf(cmp("The amount of Damage on interaction")))),
        )
    )

    private val settings: MutableMap<@Serializable(UUIDSerializer::class) UUID, ChallengeData> = mutableMapOf()

    private val challengeDamageOnChest = CustomChallengeData(
        challengeUUID,
        DamageOnChest(challengeUUID),
        getSettings(challengeUUID),
        Icon("CHEST", naming = IconNaming(cmp("Chest Damage"), listOf(cmp("You get damage on chest"), cmp("interactions")))),
        setOf(ChallengeTags.FREE)
    )


    fun loadChallenges() {
        val api = MChallengeAPI.instance
        if (api == null) {
            console.sendMessage(cmp("Failed to connect with MUtils-Challenge API!", NamedTextColor.RED))
            return
        }

        if (configFile.exists()) {
            try {
                settings.putAll(Json.decodeFromString<Map<@Serializable(UUIDSerializer::class) UUID, ChallengeData>>(configFile.readText()))
            } catch (e: Exception) {
                console.sendMessage(cmp("Failed to read settings!"))
                console.sendMessage(cmp(e.message ?: "Reason Unknown"))
            }
        }
        api.addChallenge(challengeUUID, challengeDamageOnChest)
        console.sendMessage(cmp("MAddon successfully hooked in!"))
    }

    fun saveChallenges() {
        configFile.writeText(Json.encodeToString(settings))
    }

    fun getSettings(uuid: UUID): ChallengeData {
        return settings.getOrPut(uuid) { settingsDefaults[uuid] ?: ChallengeData() }
    }
}