package de.miraculixx.mutils.modules

import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.modules.mods.fly.FLY
import de.miraculixx.mutils.modules.mods.rocket.Rocket
import de.miraculixx.mutils.modules.mods.checkpoints.Checkpoints
import de.miraculixx.mutils.modules.mods.dimSwap.DimSwap
import de.miraculixx.mutils.modules.mods.disabled.Disabled
import de.miraculixx.mutils.modules.mods.ghost.Ghost
import de.miraculixx.mutils.modules.mods.huntMob.MobHunt
import de.miraculixx.mutils.modules.mods.inTime.InTime
import de.miraculixx.mutils.modules.mods.mirror.Mirror
import de.miraculixx.mutils.modules.mods.mobBlocks.MobBlocks
import de.miraculixx.mutils.modules.mods.snake.Snake
import de.miraculixx.mutils.modules.mods.trafficlight.TrafficLight
import de.miraculixx.mutils.modules.mods.tron.Tron
import de.miraculixx.mutils.modules.mods.vampire.Vampire
import de.miraculixx.mutils.utils.cotm
import de.miraculixx.mutils.utils.settings.challenges
import de.miraculixx.mutils.utils.settings.getSetting

class StatusChanger {

    private fun getClass(module: Challenges): Challenge {
        return when (module) {
            Challenges.FLY -> FLY()
            Challenges.IN_TIME -> InTime()
            Challenges.MOB_BLOCKS -> MobBlocks()
            Challenges.CHECKPOINTS -> Checkpoints()
            Challenges.DIM_SWAP -> DimSwap()
            Challenges.SNAKE -> Snake()
            Challenges.REALISTIC -> TODO()
            //Challenges.CAPTIVE -> Captive()
            Challenges.GHOST -> Ghost()
            Challenges.BLOCK_ASYNC -> TODO()
            Challenges.NO_SAME_ITEM -> TODO()
            Challenges.LIMITED_SKILLS -> TODO()
            Challenges.RUN_RANDOMIZER -> TODO()
            Challenges.SPLIT_HP -> TODO()
            Challenges.DAMAGE_DUELL -> TODO()
            Challenges.ONE_BIOME -> TODO()
            Challenges.BOOST_UP -> TODO()
            Challenges.RIGHT_TOOL -> TODO()
            Challenges.CHUNK_BLOCK_BREAK -> TODO()
            Challenges.SNEAK_SPAWN -> TODO()
            Challenges.WORLD_PEACE -> TODO()
            Challenges.GRAVITY -> TODO()
            Challenges.STAY_AWAY -> TODO()
            Challenges.RANDOMIZER_BLOCK -> TODO()
            Challenges.RANDOMIZER_ENTITY -> TODO()
            Challenges.RANDOMIZER_BIOMES -> TODO()
            Challenges.RANDOMIZER_MOBS -> TODO()
            Challenges.FORCE_COLLECT -> TODO()
            Challenges.RANDOMIZER_DAMAGE -> TODO()
            Challenges.NO_DOUBLE_KILL -> TODO()
            Challenges.DAMAGER -> TODO()
            Challenges.RIVALS_COLLECT -> TODO()
            Challenges.ROCKET -> Rocket()
            Challenges.VAMPIRE -> Vampire()
            Challenges.TRAFFIC_LIGHT -> TrafficLight()
            Challenges.TRON -> Tron()
            Challenges.DISABLED -> Disabled()
            Challenges.MOB_HUNT -> MobHunt()
            Challenges.MIRROR -> Mirror()
        }
    }

    fun startChallenges(): ArrayList<Challenge>? {
        val activated = ArrayList<Challenge>()
        var success = false
        val available = if (false) arrayOf(cotm) else Challenges.values() //TODO

        available.forEach {
            val settings = challenges.getSetting(it)
            if (!settings.active) return@forEach

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

    fun stopChallenges(list: List<Challenge>) {
        unregisterChallenges(list)
        list.forEach {
            it.stop()
        }
    }

    fun registerChallenges(list: List<Challenge>) {
        list.forEach {
            it.register()
        }
    }

    fun unregisterChallenges(list: List<Challenge>) {
        list.forEach {
            it.unregister()
        }
    }
}