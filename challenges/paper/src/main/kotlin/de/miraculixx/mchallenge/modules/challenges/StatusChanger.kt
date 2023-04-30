package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.mchallenge.modules.mods.anvilCrusher.AnvilCrusher
import de.miraculixx.mchallenge.modules.mods.areaTimer.AreaTimer
import de.miraculixx.mchallenge.modules.mods.blockAsync.BlockAsync
import de.miraculixx.mchallenge.modules.mods.boostUp.BoostUp
import de.miraculixx.mchallenge.modules.mods.checkpoints.Checkpoints
import de.miraculixx.mchallenge.modules.mods.chunkBreaker.ChunkBlockBreaker
import de.miraculixx.mchallenge.modules.mods.chunkClearer.ChunkClearer
import de.miraculixx.mchallenge.modules.mods.chunkDecay.ChunkDecay
import de.miraculixx.mchallenge.modules.mods.chunkFlattener.ChunkFlattener
import de.miraculixx.mchallenge.modules.mods.collectBattle.CollectBattle
import de.miraculixx.mchallenge.modules.mods.damageDuel.DamageDuell
import de.miraculixx.mchallenge.modules.mods.damager.Damager
import de.miraculixx.mchallenge.modules.mods.dimSwap.DimSwap
import de.miraculixx.mchallenge.modules.mods.disabled.Disabled
import de.miraculixx.mchallenge.modules.mods.fly.FLY
import de.miraculixx.mchallenge.modules.mods.forceHunt.ForceHunt
import de.miraculixx.mchallenge.modules.mods.ghost.Ghost
import de.miraculixx.mchallenge.modules.mods.gravity.GravityManager
import de.miraculixx.mchallenge.modules.mods.huntItems.ItemHunt
import de.miraculixx.mchallenge.modules.mods.huntMob.MobHunt
import de.miraculixx.mchallenge.modules.mods.inTime.InTime
import de.miraculixx.mchallenge.modules.mods.itemDecay.ItemDecay
import de.miraculixx.mchallenge.modules.mods.limitedSkills.LimitedSkills
import de.miraculixx.mchallenge.modules.mods.mirror.Mirror
import de.miraculixx.mchallenge.modules.mods.mobBlocks.MobBlocks
import de.miraculixx.mchallenge.modules.mods.noDoubleKill.NoDoubleKills
import de.miraculixx.mchallenge.modules.mods.noSameItems.NoSameItem
import de.miraculixx.mchallenge.modules.mods.oneBiome.OneBiome
import de.miraculixx.mchallenge.modules.mods.randomizer.*
import de.miraculixx.mchallenge.modules.mods.rightTools.RightTools
import de.miraculixx.mchallenge.modules.mods.rivalCollect.RivalCollect
import de.miraculixx.mchallenge.modules.mods.rocket.Rocket
import de.miraculixx.mchallenge.modules.mods.runRandom.RunRandomizer
import de.miraculixx.mchallenge.modules.mods.snake.Snake
import de.miraculixx.mchallenge.modules.mods.sneakSpawn.SneakSpawn
import de.miraculixx.mchallenge.modules.mods.stayAway.StayAway
import de.miraculixx.mchallenge.modules.mods.trafficlight.TrafficLight
import de.miraculixx.mchallenge.modules.mods.tron.Tron
import de.miraculixx.mchallenge.modules.mods.vampire.Vampire
import de.miraculixx.mchallenge.utils.cotm
import de.miraculixx.mchallenge.utils.getAccountStatus
import de.miraculixx.mvanilla.messages.*

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
            Challenges.GHOST -> Ghost()
            Challenges.BLOCK_ASYNC -> BlockAsync()
            Challenges.NO_SAME_ITEM -> NoSameItem()
            Challenges.LIMITED_SKILLS -> LimitedSkills()
            Challenges.RUN_RANDOMIZER -> RunRandomizer()
            Challenges.DAMAGE_DUELL -> DamageDuell()
            Challenges.ONE_BIOME -> OneBiome()
            Challenges.BOOST_UP -> BoostUp()
            Challenges.RIGHT_TOOL -> RightTools()
            Challenges.CHUNK_BLOCK_BREAK -> ChunkBlockBreaker()
            Challenges.SNEAK_SPAWN -> SneakSpawn()
            Challenges.WORLD_PEACE -> TODO()
            Challenges.GRAVITY -> GravityManager()
            Challenges.STAY_AWAY -> StayAway()
            Challenges.RANDOMIZER_BLOCK -> BlockRandomizer()
            Challenges.RANDOMIZER_ENTITY -> DropsRandomizer()
            Challenges.RANDOMIZER_BIOMES -> BiomeRandomizer()
            Challenges.RANDOMIZER_MOBS -> MobSwitchRandomizer()
            Challenges.RANDOMIZER_DAMAGE -> EntityDamageRandomizer()
            Challenges.FORCE_COLLECT -> ForceHunt()
            Challenges.NO_DOUBLE_KILL -> NoDoubleKills()
            Challenges.DAMAGER -> Damager()
            Challenges.RIVALS_COLLECT -> RivalCollect()
            Challenges.ROCKET -> Rocket()
            Challenges.VAMPIRE -> Vampire()
            Challenges.TRAFFIC_LIGHT -> TrafficLight()
            Challenges.TRON -> Tron()
            Challenges.DISABLED -> Disabled()
            Challenges.MOB_HUNT -> MobHunt()
            Challenges.ITEM_HUNT -> ItemHunt()
            Challenges.MIRROR -> Mirror()
            Challenges.CHUNK_FLATTENER -> ChunkFlattener()
            Challenges.CHUNK_DECAY -> ChunkDecay()
            Challenges.CHUNK_CLEARER -> ChunkClearer()
            Challenges.ANVIL_CRUSHER -> AnvilCrusher()
            Challenges.ITEM_DECAY -> ItemDecay()
            Challenges.AREA_TIMER -> AreaTimer()
            Challenges.COLLECT_BATTLE -> CollectBattle()
        }
    }

    private fun getStatus() = getAccountStatus()

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
                if (it != cotm && !it.matchingFilter(ChallengeTags.FREE)) {
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