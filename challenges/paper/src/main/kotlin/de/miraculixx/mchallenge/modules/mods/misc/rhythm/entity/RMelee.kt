package de.miraculixx.mchallenge.modules.mods.misc.rhythm.entity

import de.miraculixx.mvanilla.messages.cmp
import org.bukkit.entity.Mob

class RMelee(private val mob: Mob) : RRandomMovement(mob) {
    var isStunned = false

    override fun tick() {
        super.tick()
        if (isStunned) {
            mob.customName(cmp("Stunned"))
            isStunned = false
            return
        }

        val player = mob.location.getNearbyPlayers(15.0).firstOrNull()
        if (player == null) {
            mob.customName(cmp("Target: Empty"))
            moveRandom()
            return
        }

        mob.pathfinder.stopPathfinding()
        val path = mob.pathfinder.findPath(player)
        if (path == null) {
            mob.customName(cmp("Target: Unreachable"))
            moveRandom()
            return
        }

        val nextPoint = (path.points.getOrNull(path.nextPointIndex + 1) ?: path.finalPoint)?.add(0.5,0.0,0.5)
        if (nextPoint == null) {
            mob.customName(cmp("Target: Reached"))
            return
        }
        mob.customName(cmp("Target: Moving Step"))

        if (nextPoint.getNearbyLivingEntities(0.1).isEmpty()) {
            mob.teleport(nextPoint)
        }
        mob.location.getNearbyPlayers(2.0).forEach { p -> mob.attack(p) }
    }
}