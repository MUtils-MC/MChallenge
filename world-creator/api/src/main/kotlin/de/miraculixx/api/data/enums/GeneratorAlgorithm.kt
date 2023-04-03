package de.miraculixx.api.data.enums

import de.miraculixx.api.data.enums.AlgorithmSetting.*
import de.miraculixx.api.data.enums.AlgorithmSettingIndex.*
import de.miraculixx.mvanilla.gui.Head64

enum class GeneratorAlgorithm(val settings: Map<AlgorithmSettingIndex, AlgorithmSetting>, val icon: String) {
    //Geometry Cuts
    LINE(           mapOf(X1 to SOLID_THICKNESS, X2 to HOLE_THICKNESS, MODE to X_DIRECTION, INVERT to INVERTED), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFjOGI3ZGVhMDE0NjdjYjdmZDlhM2RlYmQ2YTEyNzE0YTlhNDJhOGZkZmJkZGE1MzdmZmI3NTEwMzE4MzIyNSJ9fX0"),
    DIAGONAL_LINE(  mapOf(), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzcyOWEyOGYxM2IzYzcyMzQ2YWFjNWM2NjEwZDQyNTkxZTkwMTAzZmQxMmJjNzhlOGMzMGExZTFkMDFkNDY2MyJ9fX0"),
    CHESS(          mapOf(X1 to SCALE_X), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjEyYzlhMmRkMjk1YTRjYzhiNDBlMTE2M2RmOTY3YzNkYWZmZjA5YzkxMzRmNDhlODFhOGRjMzEzMGEyYWUxZSJ9fX0"),
    SQUARE(         mapOf(X1 to SCALE_X, X2 to SCALE_Z, RND to RANDOM), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY1Mjc5NzI5MzU5OTMwNWQ1Zjk0YTYxYTRhNzc4YzNmMmZhMmQ1ODVmMmRlMzhmYjA2YTQxMjUxYjRjODJmNCJ9fX0"),
    CIRCLE(         mapOf(), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI1NTc4NWEwMWJiMWNjNjQ0NGQzMWQ1ZDhhNTgwZDUxNWFmYTg5YWJhMGQxZjQwYTc0NDUxMzAyYmQzNWM0MCJ9fX0"),
    SINUS(          mapOf(X1 to SCALE_X, X2 to SCALE_Z, X3 to HEIGHT), Head64.X_RED.value),
    ;
}