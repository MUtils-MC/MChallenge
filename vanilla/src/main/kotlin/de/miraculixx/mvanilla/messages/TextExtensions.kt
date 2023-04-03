package de.miraculixx.mvanilla.messages


/**
 * Input: GRASS_BLOCK
 *
 * Output: Grass Block
 */
fun String.fancy(): String {
    val split = split('_') //GRASS_BLOCK -> [GRASS, BLOCK]
    return buildString {
        split.forEach { word ->
            append(word[0].uppercase() + word.substring(1).lowercase() + " ") //GRASS -> Grass
        }
    }.removeSuffix(" ")
}