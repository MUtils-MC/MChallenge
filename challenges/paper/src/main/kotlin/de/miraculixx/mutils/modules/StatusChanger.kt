package de.miraculixx.mutils.modules

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.modules.challenge.mods.*
import de.miraculixx.mutils.modules.challenge.mods.checkpoints.Checkpoints
import de.miraculixx.mutils.modules.challenge.mods.dimSwap.DimSwap
import de.miraculixx.mutils.modules.challenge.mods.force.ForceCollect
import de.miraculixx.mutils.modules.challenge.mods.ghost.Ghost
import de.miraculixx.mutils.modules.challenge.mods.gravity.GravityManager
import de.miraculixx.mutils.modules.challenge.mods.inTime.InTime
import de.miraculixx.mutils.modules.challenge.mods.limitedSkills.LimitedSkills
import de.miraculixx.mutils.modules.challenge.mods.mobRandomizer.MobRandomizer
import de.miraculixx.mutils.modules.challenge.mods.noSameItems.NoSameItem
import de.miraculixx.mutils.modules.challenge.mods.oneBiome.OneBiome
import de.miraculixx.mutils.modules.challenge.mods.randomizer.*
import de.miraculixx.mutils.modules.challenge.mods.realistic.Realistic
import de.miraculixx.mutils.modules.challenge.mods.runRandomizer.RunRandomizer
import de.miraculixx.mutils.modules.challenge.mods.snake.Snake
import de.miraculixx.mutils.modules.challenge.mods.worldPeace.WorldPeace

class StatusChanger {

    fun getClass(module: Challenge): Challenge? {
        return when (module) {
            Challenge.FLY -> FLY()
            Challenge.IN_TIME -> InTime()
            Challenge.MOB_RANDOMIZER -> MobRandomizer()
            Challenge.CHECKPOINTS -> Checkpoints()
            Challenge.DIM_SWAP -> DimSwap()
            Challenge.SNAKE -> Snake()
            Challenge.REALISTIC -> Realistic()
            Challenge.GHOST -> Ghost()
            Challenge.BLOCK_ASYNC -> BlockAsync()
            Challenge.NO_SAME_ITEM -> NoSameItem()
            Challenge.LIMITED_SKILLS -> LimitedSkills()
            Challenge.RUN_RANDOMIZER -> RunRandomizer()
            Challenge.SPLIT_HP -> SplitHP()
            Challenge.DAMAGE_DUELL -> DamageDuell()
            Challenge.ONE_BIOME -> OneBiome()
            Challenge.BOOST_UP -> BoostUp()
            Challenge.RIGHT_TOOL -> RightTools()
            Challenge.CHUNK_BLOCK_BREAK -> ChunkBlockBreaker()
            Challenge.SNEAK_SPAWN -> SneakSpawn()
            Challenge.WORLD_PEACE -> WorldPeace()
            Challenge.STAY_AWAY -> StayAway()
            Challenge.RANDOMIZER_BLOCK -> BlockRandomizer()
            Challenge.RANDOMIZER_ENTITY -> DropsRandomizer()
            Challenge.RANDOMIZER_BIOMES -> BiomeRandomizer()
            Challenge.RANDOMIZER_MOBS -> MobSwitchRandomizer()
            Challenge.FORCE_COLLECT -> ForceCollect()
            Challenge.RANDOMIZER_ENTITY_DAMAGE -> EntityDamageRandomizer()
            Challenge.NO_DOUBLE_KILL -> NoDoubleKills()
            Challenge.DAMAGER -> Damager()
            Challenge.GRAVITY -> GravityManager()
            Challenge.RIVALS_COLLECT -> RivalCollect()

            Challenge.CAPTIVE -> TODO("Challenge is in Alpha Mode and not yet playable")
            else -> null
        }
    }

    fun startChallenges(): ArrayList<Challenge>? {
        val activated = ArrayList<Challenge>()
        var success = false

        ChallengeManager.getActiveModules().forEach {
            val challenge = getClass(it) ?: return@forEach
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