package de.miraculixx.mutils.system.boot

import net.axay.kspigot.extensions.onlineSenders
import net.axay.kspigot.runnables.task

class DemoAlert {
    init {
        run()
    }

    private fun run() {
        val broadcaster = "\n§9§m                                            \n" +
                "§9§lMUtils Demo\n\n" +
                "§9MUtils Unlimited §7->§b https://mutils.de/m/shop\n" +
                "§9MUtils Overview §7->§b https://mutils.de/\n" +
                "§9§m                                            "
        task(false, 20 * 600, 20 * 600) {
            onlineSenders.forEach {
                it.sendMessage(broadcaster)
            }
        }
    }
}