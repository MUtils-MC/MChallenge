@file:Suppress("SameParameterValue")

package de.miraculixx.mutils.utils.tools

class TPSCalculation : Runnable {

    private var tickCount = 0
    private var ticks = LongArray(600)

    fun getTPS(): Double {
        return getTPS(100)
    }

    private fun getTPS(ticks: Int): Double {
        if (tickCount < ticks) {
            return 20.0
        }
        val target = (tickCount - 1 - ticks) % this.ticks.size
        val elapsed = System.currentTimeMillis() - this.ticks[target]
        return (ticks / (elapsed / 1000.0)).round(2) + 0.50
    }

    override fun run() {
        ticks[tickCount % ticks.size] = System.currentTimeMillis()
        tickCount += 1
    }
}