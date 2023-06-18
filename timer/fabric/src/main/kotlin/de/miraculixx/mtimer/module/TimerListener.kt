@file:Suppress("unused")

package de.miraculixx.mtimer.module

import de.miraculixx.mtimer.data.Punishment
import de.miraculixx.mtimer.events.CustomPlayer
import de.miraculixx.mtimer.events.CustomServer
import de.miraculixx.mtimer.events.CustomWorld
import de.miraculixx.mtimer.server
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.goals
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mutils.gui.await.AwaitChatMessage
import de.miraculixx.mutils.gui.utils.native
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.silkmc.silk.core.event.Events
import net.silkmc.silk.core.event.Player
import net.silkmc.silk.core.event.Server
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.broadcastText
import kotlin.time.Duration.Companion.seconds

object TimerListener {
    val worldTick = Events.CustomServer.preWorldTick.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val blockPlace = Events.CustomPlayer.preBlockPlace.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val blockBreak = Events.CustomPlayer.preBlockBreak.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onDamage = Events.CustomPlayer.onFinalDamage.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onHunger = Events.CustomPlayer.preHungerChange.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onItemUse = Events.CustomPlayer.preUseItem.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onLeave = Events.Player.preQuit.listen { _ ->
        if (goals.emptyServer && server.playerCount - 1 <= 0) {
            if (globalTimerState) pauseGlobalTimer()
        }
    }

    val onChat = Events.CustomPlayer.preSendChatMessage.listen { event ->
        val message = event.message.message()
        val sender = event.player
        AwaitChatMessage.awaitingMessages.forEach { (_, awaiter) -> awaiter.triggerChat(sender, message) }
    }

    val onDeath = Events.CustomWorld.afterMobDeath.listen { event ->
        val entity = event.entity
        val globalTimer = TimerManager.globalTimer
        when (entity.type) {
            EntityType.ENDER_DRAGON -> if (goals.enderDragon) finished(entity, globalTimer, "Ender Dragon")
            EntityType.WITHER -> if (goals.wither) finished(entity, globalTimer, "Wither")
            EntityType.ELDER_GUARDIAN -> if (goals.elderGuardian) finished(entity, globalTimer, "Elder Guardian")
            EntityType.WARDEN -> if (goals.warden) finished(entity, globalTimer, "Warden")
            EntityType.PLAYER -> {
                val player = entity as ServerPlayer
                val timer = TimerManager.globalTimer
                if (goals.playerDeath) {
                    val loc = player.blockPosition()
                    val exactLoc = player.position()
                    val world = player.level as? ServerLevel
                    timer.running = false
                    val dash = cmp("\n======================\n", NamedTextColor.DARK_AQUA, bold = true, strikethrough = true)
                    var cmp = dash + cmp(msgString("event.gameOver", listOf(player.scoreboardName)), cError, bold = true)

                    if (rules.announceLocation)
                        cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp(msgString("event.location"), NamedTextColor.GOLD, true) + cmp(
                            "${loc.x} ${loc.y} ${loc.z}",
                            NamedTextColor.YELLOW
                        ))
                            .addHover(
                                cmp(msgString("event.exactLocation"), cHighlight) + cmp(" ${exactLoc.x} ${exactLoc.y} ${exactLoc.z} \n") +
                                        cmp(msgString("event.world"), cHighlight) + cmp(" ${player.level.dimension().key().asString()}")
                            )

                    if (rules.announceSeed) {
                        val seed = world?.seed?.toString() ?: "Unknown"
                        cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp("Seed: ", NamedTextColor.GOLD, true) + cmp(seed, NamedTextColor.YELLOW))
                            .addHover(cmp(msgString("event.clickToCopy", listOf(seed)), cHighlight))
                            .clickEvent(ClickEvent.copyToClipboard(seed))
                    }

                    cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + cmp(msgString("event.playtime"), NamedTextColor.GOLD, true) + cmp(timer.buildSimple(), NamedTextColor.YELLOW)

                    if (rules.announceBack) {
                        val cmd = "/execute in ${world} run teleport @s ${loc.x} ${loc.y} ${loc.z}"
                        cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp("") + msg("event.backPrompt").addHover(cmp(cmd)).clickEvent(ClickEvent.runCommand(cmd)).color(NamedTextColor.GOLD))
                    }

                    server.broadcastText((cmp + dash).native())
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
        }
    }

    private fun finished(entity: LivingEntity, timer: Timer, type: String) {
        //val tool = ChallengeManager()
        //tool.stopChallenges(ModuleManager.getChallenges())
        //challenges = ChallengeStatus.PAUSED
        val dash = cmp("\n======================\n", NamedTextColor.DARK_AQUA, bold = true, strikethrough = true)
        val dashes = cmp("\n>> ", NamedTextColor.DARK_GRAY)
        var final = dash + cmp(msgString("event.endSuccess"), cSuccess, true) +
                dashes + cmp(type, NamedTextColor.GOLD, true)

        if (rules.announceSeed) {
            val seed = (entity.level as ServerLevel).seed.toString()
            final += dashes + (cmp("Seed: ", NamedTextColor.GOLD, true) + cmp(seed, NamedTextColor.YELLOW))
                .addHover(cmp(msgString("event.clickCopy"), cHighlight))
                .clickEvent(ClickEvent.copyToClipboard(seed))
        }

        server.broadcastText((final + dashes + cmp(msgString("event.playtime"), NamedTextColor.GOLD, true) + cmp(" ${timer.buildSimple()}") + dash).native())

        mcCoroutineTask(delay = 1.seconds) {
            timer.running = false
        }
    }
}