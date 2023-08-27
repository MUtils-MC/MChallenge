package de.miraculixx.mtimer.vanilla.data

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    var language: String = "en_US"
)

@Serializable
data class Rules(
    var freezeWorld: Boolean = true,
    var announceSeed: Boolean = true,
    var announceLocation: Boolean = true,
    var announceBack: Boolean = true,
    var specOnDeath: Boolean = true,
    var specOnJoin: Boolean = false,
    var punishmentSetting: PunishmentSetting = PunishmentSetting(),
    var syncWithChallenge: Boolean = true,
)

@Serializable
data class Goals(
    var enderDragon: Boolean = true,
    var wither: Boolean = false,
    var elderGuardian: Boolean = false,
    var warden: Boolean = false,
    var playerDeath: Boolean = true,
    var emptyServer: Boolean = true,
)

@Serializable
data class PunishmentSetting(
    var active: Boolean = false,
    var type: Punishment = Punishment.BAN
)