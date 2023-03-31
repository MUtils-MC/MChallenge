package de.miraculixx.mutils.modules.spectator

import de.miraculixx.api.modules.spectator.Activation
import de.miraculixx.api.modules.spectator.Visibility
import kotlinx.serialization.Serializable

@Serializable
data class SpecCollection(
    var hide: Visibility = Visibility.HIDDEN,
    var selfHide: Visibility = Visibility.SHOWN,
    var itemPickup: Activation = Activation.DISABLED,
    var blockBreak: Activation = Activation.DISABLED,
    var flySpeed: Int = 1,
)