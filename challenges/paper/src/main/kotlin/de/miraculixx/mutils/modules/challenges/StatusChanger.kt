package de.miraculixx.mutils.modules.challenges

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.modules.challenges.StatusChangerAPI
import de.miraculixx.mutils.modules.mods.anvilCrusher.AnvilCrusher
import de.miraculixx.mutils.modules.mods.areaTimer.AreaTimer
import de.miraculixx.mutils.modules.mods.blockAsync.BlockAsync
import de.miraculixx.mutils.modules.mods.boostUp.BoostUp
import de.miraculixx.mutils.modules.mods.fly.FLY
import de.miraculixx.mutils.modules.mods.rocket.Rocket
import de.miraculixx.mutils.modules.mods.checkpoints.Checkpoints
import de.miraculixx.mutils.modules.mods.chunkBreaker.ChunkBlockBreaker
import de.miraculixx.mutils.modules.mods.chunkClearer.ChunkClearer
import de.miraculixx.mutils.modules.mods.chunkDecay.ChunkDecay
import de.miraculixx.mutils.modules.mods.chunkFlattener.ChunkFlattener
import de.miraculixx.mutils.modules.mods.damager.Damager
import de.miraculixx.mutils.modules.mods.dimSwap.DimSwap
import de.miraculixx.mutils.modules.mods.disabled.Disabled
import de.miraculixx.mutils.modules.mods.ghost.Ghost
import de.miraculixx.mutils.modules.mods.gravity.GravityManager
import de.miraculixx.mutils.modules.mods.huntItems.ItemHunt
import de.miraculixx.mutils.modules.mods.huntMob.MobHunt
import de.miraculixx.mutils.modules.mods.inTime.InTime
import de.miraculixx.mutils.modules.mods.itemDecay.ItemDecay
import de.miraculixx.mutils.modules.mods.limitedSkills.LimitedSkills
import de.miraculixx.mutils.modules.mods.mirror.Mirror
import de.miraculixx.mutils.modules.mods.mobBlocks.MobBlocks
import de.miraculixx.mutils.modules.mods.noSameItems.NoSameItem
import de.miraculixx.mutils.modules.mods.randomizer.*
import de.miraculixx.mutils.modules.mods.rightTools.RightTools
import de.miraculixx.mutils.modules.mods.runRandom.RunRandomizer
import de.miraculixx.mutils.modules.mods.snake.Snake
import de.miraculixx.mutils.modules.mods.sneakSpawn.SneakSpawn
import de.miraculixx.mutils.modules.mods.stayAway.StayAway
import de.miraculixx.mutils.modules.mods.trafficlight.TrafficLight
import de.miraculixx.mutils.modules.mods.tron.Tron
import de.miraculixx.mutils.modules.mods.vampire.Vampire

class StatusChanger: StatusChangerAPI {
    override fun getClass(module: Challenges): Challenge {
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
            Challenges.BLOCK_ASYNC -> BlockAsync()
            Challenges.NO_SAME_ITEM -> NoSameItem()
            Challenges.LIMITED_SKILLS -> LimitedSkills()
            Challenges.RUN_RANDOMIZER -> RunRandomizer()
            Challenges.DAMAGE_DUELL -> TODO()
            Challenges.ONE_BIOME -> TODO()
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
            Challenges.FORCE_COLLECT -> TODO()
            Challenges.NO_DOUBLE_KILL -> TODO()
            Challenges.DAMAGER -> Damager()
            Challenges.RIVALS_COLLECT -> TODO()
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
        }
    }
}