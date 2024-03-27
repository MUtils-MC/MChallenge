package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.mods.force.forceHunt.ForceHunt
import de.miraculixx.mchallenge.modules.mods.force.huntItems.ItemHunt
import de.miraculixx.mchallenge.modules.mods.force.huntMob.MobHunt
import de.miraculixx.mchallenge.modules.mods.force.itemDecay.ItemDecay
import de.miraculixx.mchallenge.modules.mods.misc.anvilCrusher.AnvilCrusher
import de.miraculixx.mchallenge.modules.mods.misc.areaTimer.AreaTimer
import de.miraculixx.mchallenge.modules.mods.misc.blockWall.BlockWall
import de.miraculixx.mchallenge.modules.mods.misc.checkpoints.Checkpoints
import de.miraculixx.mchallenge.modules.mods.misc.ghost.Ghost
import de.miraculixx.mchallenge.modules.mods.misc.gravity.GravityManager
import de.miraculixx.mchallenge.modules.mods.misc.inTime.InTime
import de.miraculixx.mchallenge.modules.mods.misc.realistic.Realistic
import de.miraculixx.mchallenge.modules.mods.misc.rhythm.RhythmCraft
import de.miraculixx.mchallenge.modules.mods.misc.rocket.Rocket
import de.miraculixx.mchallenge.modules.mods.misc.snake.Snake
import de.miraculixx.mchallenge.modules.mods.misc.trafficlight.TrafficLight
import de.miraculixx.mchallenge.modules.mods.misc.vampire.Vampire
import de.miraculixx.mchallenge.modules.mods.multiplayer.blockAsync.BlockAsync
import de.miraculixx.mchallenge.modules.mods.multiplayer.collectBattle.CollectBattle
import de.miraculixx.mchallenge.modules.mods.multiplayer.damageDuel.DamageDuell
import de.miraculixx.mchallenge.modules.mods.multiplayer.hitOrder.HitOrder
import de.miraculixx.mchallenge.modules.mods.multiplayer.limitedSkills.LimitedSkills
import de.miraculixx.mchallenge.modules.mods.multiplayer.mirror.Mirror
import de.miraculixx.mchallenge.modules.mods.multiplayer.noSameItems.NoSameItem
import de.miraculixx.mchallenge.modules.mods.multiplayer.rivalCollect.RivalCollect
import de.miraculixx.mchallenge.modules.mods.multiplayer.tron.Tron
import de.miraculixx.mchallenge.modules.mods.randomizer.*
import de.miraculixx.mchallenge.modules.mods.randomizer.mobBlocks.MobBlocks
import de.miraculixx.mchallenge.modules.mods.randomizer.runRandom.RunRandomizer
import de.miraculixx.mchallenge.modules.mods.randomizer.sneakSpawn.SneakSpawn
import de.miraculixx.mchallenge.modules.mods.simple.boostUp.BoostUp
import de.miraculixx.mchallenge.modules.mods.simple.damageMultiplier.DamageMultiplier
import de.miraculixx.mchallenge.modules.mods.simple.damager.Damager
import de.miraculixx.mchallenge.modules.mods.simple.disabled.Disabled
import de.miraculixx.mchallenge.modules.mods.simple.fly.FLY
import de.miraculixx.mchallenge.modules.mods.simple.noDoubleKill.NoDoubleKills
import de.miraculixx.mchallenge.modules.mods.simple.rightTools.RightTools
import de.miraculixx.mchallenge.modules.mods.simple.stayAway.StayAway
import de.miraculixx.mchallenge.modules.mods.simple.tickRate.TickRateChanger
import de.miraculixx.mchallenge.modules.mods.worldChanging.blockWorld.BlockWorld
import de.miraculixx.mchallenge.modules.mods.worldChanging.chunkBreaker.ChunkBlockBreaker
import de.miraculixx.mchallenge.modules.mods.worldChanging.chunkClearer.ChunkClearer
import de.miraculixx.mchallenge.modules.mods.worldChanging.chunkDecay.ChunkDecay
import de.miraculixx.mchallenge.modules.mods.worldChanging.chunkFlattener.ChunkFlattener
import de.miraculixx.mchallenge.modules.mods.worldChanging.chunkSync.ChunkSync
import de.miraculixx.mchallenge.modules.mods.worldChanging.dimSwap.DimSwap
import de.miraculixx.mchallenge.modules.mods.worldChanging.lowVision.LowVision
import de.miraculixx.mchallenge.modules.mods.worldChanging.mineField.MineFieldWorld
import de.miraculixx.mchallenge.modules.mods.worldChanging.oneBiome.OneBiome
import de.miraculixx.mchallenge.modules.mods.worldChanging.worldDecay.WorldDecay
import de.miraculixx.mchallenge.utils.UniversalChallenge
import de.miraculixx.mchallenge.utils.cotm
import de.miraculixx.mchallenge.utils.getAccountStatus
import de.miraculixx.mvanilla.messages.*
import java.util.*

class StatusChanger {
    private fun getClass(module: Challenges): Challenge {
        return when (module) {
            Challenges.FLY -> FLY()
            Challenges.IN_TIME -> InTime()
            Challenges.MOB_BLOCKS -> MobBlocks()
            Challenges.CHECKPOINTS -> Checkpoints()
            Challenges.DIM_SWAP -> DimSwap()
            Challenges.SNAKE -> Snake()
            Challenges.REALISTIC -> Realistic()
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
            Challenges.GRAVITY -> GravityManager()
            Challenges.STAY_AWAY -> StayAway()
            Challenges.RANDOMIZER_BLOCK -> BlockRandomizer()
            Challenges.RANDOMIZER_ENTITY -> DropsRandomizer()
            Challenges.RANDOMIZER_BIOMES -> BiomeRandomizer()
            Challenges.RANDOMIZER_MOBS -> MobSwitchRandomizer()
            Challenges.RANDOMIZER_DAMAGE -> EntityDamageRandomizer()
            Challenges.RANDOMIZER_CHESTS -> LootTableRandomizer()
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
            Challenges.BLOCK_WORLD -> BlockWorld()
            Challenges.MINEFIELD_WORLD -> MineFieldWorld()
            Challenges.BLOCK_WALL -> BlockWall()
            Challenges.DAMAGE_MULTIPLIER -> DamageMultiplier()
            Challenges.WORLD_DECAY -> WorldDecay()
            Challenges.LOW_VISION -> LowVision()
            Challenges.CHUNK_SYNC -> ChunkSync()
            Challenges.HIT_ORDER -> HitOrder()
            Challenges.TICK_RATE -> TickRateChanger()
            Challenges.RHYTHM_CRAFT -> RhythmCraft()
//            Challenges.HALLOWEEN -> HalloweenChallenge()
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
        val actives = Challenges.entries.filter { challenges.getSetting(it).active }
        val addons = ChallengeManager.getCustomChallenges().filter { it.value.data.active }.map { ChallengeAddon(it.value.tags, it.key, it.value.challenge) }
        val history = ChallengeManager.historyChallenges

        // Internal Challenges
        actives.forEach {
            val instance = getClass(it)
            if (startChallenge(it.filter, status, instance, it, null)) {
                success = true
                activated.add(instance)
                val universal = UniversalChallenge(it, null)
                history.remove(universal)
                history.add(0, universal)
            }
        }

        // Addon Challenges
        addons.forEach {
            if (startChallenge(it.tags, status, it.instance, null, it.uuid)) {
                success = true
                activated.add(it.instance)
                val universal = UniversalChallenge(null, it.uuid)
                history.remove(universal)
                history.add(0, universal)
            }
        }

        if (!success) {
            stopChallenges(activated)
            return null
        }
        registerChallenges(activated)
        return activated
    }

    private fun startChallenge(tags: Set<ChallengeTags>, status: Boolean, instance: Challenge, internalChallenge: Challenges?, customUUID: UUID?): Boolean {
        if (!status) {
            if (internalChallenge != cotm && !tags.contains(ChallengeTags.FREE)) {
                consoleAudience.sendMessage(prefix + cmp("Challenge ${internalChallenge?.name ?: customUUID} requires a connected account to play!", cError))
                return false
            }
        }

        return instance.start()
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

    private data class ChallengeAddon(val tags: Set<ChallengeTags>, val uuid: UUID, val instance: Challenge)
}