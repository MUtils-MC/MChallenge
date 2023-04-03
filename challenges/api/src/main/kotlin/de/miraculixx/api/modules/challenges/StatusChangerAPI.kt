package de.miraculixx.api.modules.challenges

import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.api.utils.cotm
import de.miraculixx.mvanilla.gui.StorageFilter
import de.miraculixx.mutils.messages.*
import de.miraculixx.mvanilla.messages.*

interface StatusChangerAPI {
    fun getClass(module: Challenges): Challenge
    fun getStatus(): Boolean

    /**
     * Start all loaded Challenges, as long as enough permissions are granted
     * @return A list of all enabled challenges
     */
    fun startChallenges(): ArrayList<Challenge>? {
        val activated = ArrayList<Challenge>()
        var success = false
        val status = getStatus()
        val available = Challenges.values()

        available.forEach {
            val settings = challenges.getSetting(it)
            if (!settings.active) return@forEach

            if (!status) {
                if (it != cotm && !it.matchingFilter(StorageFilter.FREE)) {
                    consoleAudience.sendMessage(prefix + cmp("Challenge ${it.name} requires a connected account to play!", cError))
                    return@forEach
                }
            }

            val challenge = getClass(it)
            if (challenge.start()) {
                success = true
                activated.add(challenge)
            }
        }

        if (!success) {
            stopChallenges(activated)
            return null
        }
        registerChallenges(activated)
        return activated
    }

    /**
     * Stop all loaded challenges
     */
    fun stopChallenges(list: List<Challenge>) {
        unregisterChallenges(list)
        list.forEach {
            it.stop()
        }
    }

    /**
     * Register all loaded challenges
     */
    fun registerChallenges(list: List<Challenge>) {
        list.forEach {
            it.register()
        }
    }

    /**
     * Unregister all loaded challenges
     */
    fun unregisterChallenges(list: List<Challenge>) {
        list.forEach {
            it.unregister()
        }
    }
}