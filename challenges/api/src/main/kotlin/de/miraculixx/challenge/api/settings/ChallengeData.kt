package de.miraculixx.challenge.api.settings

import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.utils.IconNaming
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @return All setting data for a default challenge
 */
fun MutableMap<Challenges, ChallengeData>.getSetting(challenge: Challenges): ChallengeData {
    return getOrPut(challenge) {
        ChallengeData(challenge.getDefaultSettings(), emptyMap(), false)
    }
}

/**
 * Represent all challenge data. Can be serialized with the Kotlin Serializer to save and load easily.
 * @param settings All settings - <SettingKey, SettingValue>
 * @param settingNames All namings - <SettingKey, CustomNaming>
 * @param active Challenge activity. Should not be modified by any addon
 */
@Serializable
data class ChallengeData(
    val settings: Map<String, ChallengeSetting<out @Contextual Any>> = emptyMap(),
    val settingNames: Map<String, IconNaming> = emptyMap(),
    var active: Boolean = false,
)

/**
 * @param default Default value for init and reset
 * @param value Current value (default on init)
 * @param unit Information unit (like b/s)
 */
@Serializable
data class ChallengeIntSetting(
    private val material: String = "BARRIER",
    private val default: Int = 0,
    private val unit: String? = null,
    private var value: Int = default,

    val max: Int = 100,
    val min: Int = 0,
    val step: Int = 1,
) : ChallengeSetting<Int> {
    override val materialKey = material
    override fun getDefault() = default
    override fun getUnit() = unit ?: ""
    override fun getValue() = value
    override fun setValue(new: Int) {
        value = new
    }
}

@Serializable
data class ChallengeDoubleSetting(
    private val material: String = "BARRIER",
    private val default: Double = 0.0,
    private val unit: String? = null,
    private var value: Double = default,

    val max: Double = 10.0,
    val min: Double = 0.0,
    val step: Double = 0.5,
) : ChallengeSetting<Double> {
    override val materialKey = material
    override fun getDefault() = default
    override fun getUnit() = unit ?: ""
    override fun getValue() = value
    override fun setValue(new: Double) {
        value = new
    }
}

@Serializable
data class ChallengeBoolSetting(
    private val material: String = "BARRIER",
    private val default: Boolean = false,
    private val unit: String? = null,
    private var value: Boolean = default,
) : ChallengeSetting<Boolean> {
    override val materialKey = material
    override fun getDefault() = default
    override fun getUnit() = unit ?: ""
    override fun getValue() = value
    override fun setValue(new: Boolean) {
        value = new
    }
}

@Serializable
data class ChallengeEnumSetting(
    private val material: String = "BARRIER",
    private val default: String = "NONE",
    private val unit: String? = null,
    private var value: String = default,

    val options: List<String> = emptyList(),
) : ChallengeSetting<String> {
    override val materialKey = material
    override fun getDefault() = default
    override fun getUnit() = unit ?: ""
    override fun getValue() = value
    override fun setValue(new: String) {
        value = new
    }
}


@Serializable
data class ChallengeSectionSetting<T>(
    private val material: String = "BARRIER",
    private val default: Map<String, ChallengeSetting<T>> = emptyMap(),
    private val unit: String? = null,
    private var value: Map<String, ChallengeSetting<T>> = default,
) : ChallengeSetting<Map<String, ChallengeSetting<T>>> {
    override val materialKey = material
    override fun getDefault() = default
    override fun getUnit() = unit ?: ""
    override fun getValue() = value
    override fun setValue(new: Map<String, ChallengeSetting<T>>) {}
}

@Serializable
sealed interface ChallengeSetting<T> {
    val materialKey: String
    fun getDefault(): T
    fun getUnit(): String
    fun getValue(): T
    fun setValue(new: T)

    fun toInt() = this as? ChallengeIntSetting
    fun toDouble() = this as? ChallengeDoubleSetting
    fun toBool() = this as? ChallengeBoolSetting
    fun toSection() = this as? ChallengeSectionSetting<*>
    fun toEnum() = this as? ChallengeEnumSetting
}