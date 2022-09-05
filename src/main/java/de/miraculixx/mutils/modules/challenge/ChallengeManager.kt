package de.miraculixx.mutils.modules.challenge

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenge.mods.*

class ChallengeManager {

    private fun getClass(module: Modules): Challenge? {
        return when (module) {
            Modules.FLY -> FLY()
            Modules.IN_TIME -> InTime()
            Modules.MOB_RANDOMIZER -> MobRandomizer()
            Modules.CHECKPOINTS -> Checkpoints()
            Modules.DIM_SWAP -> DimSwap()
            Modules.SNAKE -> Snake()
            Modules.REALISTIC -> Realistic()
            Modules.GHOST -> Ghost()
            Modules.BLOCK_ASYNC -> BlockAsync()
            Modules.NO_SAME_ITEM -> NoSameItem()
            Modules.LIMITED_SKILLS -> LimitedSkills()
            Modules.RUN_RANDOMIZER -> RunRandomizer()
            Modules.SPLIT_HP -> SplitHP()
            Modules.DAMAGE_DUELL -> DamageDuell()
            Modules.ONE_BIOME -> OneBiome()
            Modules.BOOST_UP -> BoostUp()
            Modules.RIGHT_TOOL -> RightTools()
            Modules.CHUNK_BLOCK_BREAK -> ChunkBlockBreaker()
            Modules.SNEAK_SPAWN -> SneakSpawn()
            Modules.WORLD_PEACE -> WorldPeace()
            Modules.STAY_AWAY -> StayAway()
            Modules.RANDOMIZER_BLOCK -> BlockRandomizer()
            Modules.RANDOMIZER_ENTITY -> DropsRandomizer()
            Modules.RANDOMIZER_BIOMES -> BiomeRandomizer()
            Modules.RANDOMIZER_MOBS -> MobSwitchRandomizer()
            Modules.FORCE_COLLECT -> ForceCollect()
            Modules.RANDOMIZER_ENTITY_DAMAGE -> EntityDamageRandomizer()
            Modules.NO_DOUBLE_KILL -> NoDoubleKills()
            Modules.DAMAGER -> Damager()
            Modules.GRAVITY -> GravityManager()
            Modules.RIVALS_COLLECT -> RivalCollect()

            Modules.CAPTIVE -> TODO("Challenge is in Alpha Mode and not yet playable")
            else -> null
        }
    }

    fun startChallenges(): ArrayList<Challenge>? {
        val activated = ArrayList<Challenge>()
        var success = false

        ModuleManager.getActiveModules().forEach {
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