package de.miraculixx.mchallenge.modules.spectator

import de.miraculixx.mchallenge.modules.spectator.data.Activation
import de.miraculixx.mchallenge.modules.spectator.data.Visibility
import kotlinx.serialization.Serializable

@Serializable
data class SpecCollection(
    var hide: Visibility = Visibility.HIDDEN,
    var selfHide: Visibility = Visibility.SHOWN,
    var itemPickup: Activation = Activation.DISABLED,
    var blockBreak: Activation = Activation.DISABLED,
    var flySpeed: Int = 1,
)