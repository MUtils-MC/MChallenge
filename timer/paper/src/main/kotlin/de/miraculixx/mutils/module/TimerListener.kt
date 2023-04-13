package de.miraculixx.mutils.module

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.geometry.toSimpleString
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.data.Punishment
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class TimerListener {

    fun disableAll() {
        onDamage.unregister()
        onDie.unregister()
        onQuit.unregister()
        onBreak.unregister()
        onHunger.unregister()
        onPlace.unregister()
        onEntityDeath.unregister()
        onInteract.unregister()
        onSpawn.unregister()
    }

    fun activateTimer() {
        onQuit.register()
        onDie.register()
        onEntityDeath.register()

        //Remove world freeze
        onDamage.unregister()
        onInteract.unregister()
        onBreak.unregister()
        onPlace.unregister()
        onSpawn.unregister()
        onHunger.unregister()
        toggleFreeze(true)
    }

    fun deactivateTimer() {
        onQuit.unregister()
        onDie.unregister()
        onEntityDeath.unregister()

        if (rules.freezeWorld) {
            onDamage.register()
            onInteract.register()
            onBreak.register()
            onPlace.register()
            onSpawn.register()
            onHunger.register()
            toggleFreeze(false)
        }
    }

    private fun toggleFreeze(active: Boolean) {
        worlds.forEach { world ->
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, active)
            world.livingEntities.forEach { entity ->
                if (entity !is Player) entity.setAI(active)
            }
        }
    }

    //
    // Timer Goals
    //
    private val onDie = listen<PlayerDeathEvent>(register = false) {
        val player = it.entity
        val timer = TimerManager.getGlobalTimer()
        //val chManger = ChallengeManager() TODO Adding Challenge API
        if (goals.playerDeath) {
            val loc = it.entity.location
            timer.running = false
            //challenges = ChallengeStatus.PAUSED
            //chManger.unregisterChallenges(ModuleManager.getChallenges())
            val dash = cmp("\n======================\n", NamedTextColor.DARK_AQUA, bold = true, strikethrough = true)
            var cmp = dash + cmp(msgString("event.gameOver", listOf(player.name)), cError, bold = true)

            if (rules.announceLocation)
                cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp(msgString("event.location"), NamedTextColor.GOLD, true) + cmp(
                    "${loc.blockX} ${loc.blockY} ${loc.blockZ}",
                    NamedTextColor.YELLOW
                ))
                    .addHover(
                        cmp(msgString("event.exactLocation"), cHighlight) + cmp(" ${loc.toSimpleString()}\n") +
                                cmp(msgString("event.world"), cHighlight) + cmp(" ${loc.world.name}")
                    )

            if (rules.announceSeed) {
                val seed = loc.world.seed.toString()
                cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp("Seed: ", NamedTextColor.GOLD, true) + cmp(seed, NamedTextColor.YELLOW))
                    .addHover(cmp(msgString("event.clickToCopy", listOf(seed)), cHighlight))
                    .clickEvent(ClickEvent.copyToClipboard(seed))
            }

            cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + cmp(msgString("event.playtime"), NamedTextColor.GOLD, true) + cmp(timer.buildSimple(), NamedTextColor.YELLOW)

            if (rules.announceBack) {
                val cmd = "/execute in ${loc.world.key().asString()} run teleport @s ${loc.blockX} ${loc.blockY} ${loc.blockZ}"
                cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp("") + msg("event.backPrompt").addHover(cmp(cmd)).clickEvent(ClickEvent.runCommand(cmd)).color(NamedTextColor.GOLD))
            }

            broadcast(cmp + dash)
        } else {
            if (rules.specOnDeath) {
                val loc = it.entity.location
                val immediateRespawn = loc.world?.getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN)
                loc.world?.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                task(true, 2, 2, 2, endCallback = {
                    loc.world?.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, immediateRespawn ?: false)
                }) { _ ->
                    it.entity.gameMode = GameMode.SPECTATOR
                    it.entity.teleport(loc)
                }
            }
        }

        val punish = rules.punishmentSetting
        if (punish.active) {
            val kickMsg = msg("event.kick", listOf(player.name))
            if (punish.type == Punishment.BAN) {
                player.banPlayer(msgString("event.ban", listOf(player.name)))
                player.kick(kickMsg)
            } else player.kick(kickMsg)
        }
    }

    private val onEntityDeath = listen<EntityDeathEvent>(register = false) {
        val entity = it.entity
        val type = entity.type
        when {
            type == EntityType.ENDER_DRAGON -> if (goals.enderDragon) finished(entity, TimerManager.getGlobalTimer())
            type == EntityType.WITHER -> if (goals.wither) finished(entity, TimerManager.getGlobalTimer())
            type == EntityType.ELDER_GUARDIAN -> if (goals.elderGuardian) finished(entity, TimerManager.getGlobalTimer())
            majorVersion >= 19 && type == EntityType.WARDEN -> if (goals.warden) finished(entity, TimerManager.getGlobalTimer())
            else -> Unit
        }
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        val pTimer = TimerManager.getPersonalTimer(it.player.uniqueId)
        pTimer?.running = false

        if (onlinePlayers.size <= 1 && goals.emptyServer) {
            TimerManager.getGlobalTimer().running = false
            console.sendMessage(msg("command.stop", listOf("Console")))
        }
    }

    private fun finished(entity: Entity, timer: Timer) {
        timer.running = false
        //val tool = ChallengeManager()
        //tool.stopChallenges(ModuleManager.getChallenges())
        //challenges = ChallengeStatus.PAUSED
        val dash = cmp("\n======================\n", NamedTextColor.DARK_AQUA, bold = true, strikethrough = true)
        val dashes = cmp("\n>> ", NamedTextColor.DARK_GRAY)
        var final = dash + cmp(msgString("event.endSuccess"), cSuccess, true) +
                dashes + cmp(entity.name, NamedTextColor.GOLD, true)

        if (rules.announceSeed) {
            val seed = entity.world.seed.toString()
            final += dashes + (cmp("Seed: ", NamedTextColor.GOLD, true) + cmp(seed, NamedTextColor.YELLOW))
                .addHover(cmp(msgString("event.clickCopy"), cHighlight))
                .clickEvent(ClickEvent.copyToClipboard(seed))
        }

        broadcast(final + dashes + cmp(msgString("event.playtime"), NamedTextColor.GOLD, true) + cmp(" ${timer.buildSimple()}") + dash)
    }


    /*
    If timer is paused, the World should freeze...
    At least, you shouldn't be able to interact with it
     */
    private val onDamage = listen<EntityDamageEvent>(register = false) {
        if (it.cause != EntityDamageEvent.DamageCause.VOID)
            it.isCancelled = true
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE && gm != GameMode.SPECTATOR
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        if (it.isCancelled) return@listen
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE
    }
    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        if (it.isCancelled) return@listen
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE
    }

    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        it.isCancelled = true
    }

    private val onHunger = listen<FoodLevelChangeEvent>(register = false) {
        it.isCancelled = true
    }
}