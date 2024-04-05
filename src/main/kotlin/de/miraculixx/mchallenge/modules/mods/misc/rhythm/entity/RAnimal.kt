package de.miraculixx.mchallenge.modules.mods.misc.rhythm.entity

import org.bukkit.entity.Animals

class RAnimal(entity: Animals) : RRandomMovement(entity) {

    override fun tick() {
        super.tick()
        moveRandom()
    }
}