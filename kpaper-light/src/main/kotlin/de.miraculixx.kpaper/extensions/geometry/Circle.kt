@file:Suppress("MemberVisibilityCanBePrivate")

package de.miraculixx.kpaper.extensions.geometry

import org.bukkit.util.Vector

/**
 * Contains the core circle generator functions.
 */
object Circle {
    /**
     * Produces positions for a hollow circle around x=0, z=0.
     */
    inline fun produceCirclePositions(radius: Int, crossinline consumer: (Vector) -> Unit) {
        val addLoc: (Int, Int) -> Unit = { first, second ->
            consumer(Vector(first, second, 0))
        }

        var d = -radius
        var x = radius
        var y = 0

        while (y <= x) {
            addLoc(x, y)
            addLoc(x, -y)
            addLoc(-x, y)
            addLoc(-x, -y)
            addLoc(y, x)
            addLoc(y, -x)
            addLoc(-y, x)
            addLoc(-y, -x)

            d += 2 * y + 1
            y++

            if (d > 0) {
                d += -2 * x + 2
                x--
            }
        }
    }

    /**
     * Produces positions for a filled circle around x=0, z=0.
     */
    inline fun produceFilledCirclePositions(radius: Int, consumer: (Vector) -> Unit) {
        for (xIter in (-radius)..(+radius))
            for (zIter in (-radius)..(+radius))
                if (
                    xIter * xIter +
                    zIter * zIter
                    < radius * radius
                ) consumer(Vector(xIter, zIter, 0))
    }

    /**
     * Builds a set using [produceCirclePositions].
     */
    fun circlePositionSet(radius: Int) =
        HashSet<Vector>().apply {
            produceCirclePositions(radius) { add(it) }
        }

    /**
     * Builds a set using [produceFilledCirclePositions].
     */
    fun filledCirclePositionSet(radius: Int) =
        HashSet<Vector>().apply {
            produceFilledCirclePositions(radius) { add(it) }
        }
}

/**
 * Produces positions for a hollow circle around this position.
 */
inline fun Vector.produceCirclePositions(radius: Int, crossinline consumer: (Vector) -> Unit) {
    val x = this.x
    val y = this.y
    val z = this.z
    Circle.produceCirclePositions(radius) {
        consumer(Vector(x + it.x, y.toDouble(), z + it.z))
    }
}

/**
 * Produces positions for a filled circle around this position.
 */
inline fun Vector.produceFilledCirclePositions(radius: Int, crossinline consumer: (Vector) -> Unit) {
    val x = this.x
    val y = this.y
    val z = this.z
    Circle.produceFilledCirclePositions(radius) {
        consumer(Vector(x + it.x, y, z + it.z))
    }
}

/**
 * Builds a set using [produceCirclePositions].
 */
fun Vector.circlePositionSet(radius: Int) =
    HashSet<Vector>().apply {
        produceCirclePositions(radius) { add(it) }
    }

/**
 * Builds a set using [produceFilledCirclePositions].
 */
fun Vector.filledCirclePositionSet(radius: Int) =
    HashSet<Vector>().apply {
        produceFilledCirclePositions(radius) { add(it) }
    }
