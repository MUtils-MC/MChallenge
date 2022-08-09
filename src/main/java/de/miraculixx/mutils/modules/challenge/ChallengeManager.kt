package de.miraculixx.mutils.modules.challenge

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.ModuleManager
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

class ChallengeManager {

    private fun getClasses(): List<Challenge> {
        /*
        ALPHA Challenges

        Captive()
        Gravity()
        Christmas()
        */
        return listOf(
            LimitedSkills(),
            Snake(),
            BlockAsync(),
            Checkpoints(),
            DamageDuell(),
            DimSwap(),
            FLY(),
            Ghost(),
            InTime(),
            MobRandomizer(),
            NoSameItem(),
            Realistic(),
            RunRandomizer(),
            SplitHP(),
            OneBiome(),
            BoostUp(),
            RightTools(),
            ChunkBlockBreaker(),
            SneakSpawn(),
            WorldPeace(),
            StayAway(),
            BlockRandomizer(),
            DropsRandomizer(),
            BiomeRandomizer(),
            MobSwitchRandomizer(),
            ForceCollect(),
            EntityDamageRandomizer(),
            NoDoubleKills(),
            Damager(),
            GravityManager(),
            RivalCollect(),
        )
    }

    fun getClass(module: Modules): Challenge? {
        getClasses().forEach {
            if (module == it.challenge)
                return it
        }
        return null
    }

    fun startChallenges(): ArrayList<Challenge>? {
        val challenges = ArrayList<Challenge>(getClasses())
        val activated = ArrayList<Challenge>()
        var success = false

        challenges.forEach {
            if (ModuleManager.isActive(it.challenge)) {
                if (it.start()) {
                    success = true
                    activated.add(it)
                } else return@forEach
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