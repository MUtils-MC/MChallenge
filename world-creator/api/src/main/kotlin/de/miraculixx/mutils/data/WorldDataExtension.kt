package de.miraculixx.mutils.data

import de.miraculixx.mutils.messages.miniMessages
import de.miraculixx.mutils.messages.msgString

fun WorldData.printInfo(isNew: Boolean) = miniMessages.deserialize(
    "<grey><gold>┐</gold> <green>${if (isNew) msgString("event.worldCreated") else msgString("event.worldInfo")}</green>\n" +
            "<gold>├></gold> ${msgString("event.name")} ≫ <blue>${worldName}</blue>\n" +
            "<gold>├></gold> ${msgString("event.category")} ≫ <blue>${category}</blue>\n" +
            "<gold>├></gold> ${msgString("event.seed")} ≫ <blue>${seed}</blue>\n" +
            "<gold>├></gold> ${msgString("event.dimension")} ≫ <blue>${msgString("event.env.${environment.name}")}</blue>\n" +
            "<gold>├></gold> ${msgString("event.type")} ≫ <blue>${msgString("event.gen.${worldType.name}")}</blue>\n" +
            "<gold>├></gold> ${msgString("event.biomeProvider")} ≫ <blue><hover:show_text:'<red>Settings TODO'>${
                msgString("items.algo.${biomeProvider.algorithm.name}.n")
            }</hover></blue>\n" +
            "<gold>└></gold> ${msgString("event.noiseProvider")} ≫ <blue><hover:show_text:'${
                buildString hover@{
                    chunkProviders.forEach { cp ->
                        val gen = cp.algorithm
                        val setting = cp.settings
                        append(
                            "<grey>- <blue>${msgString("items.creator.${gen.name}.n")}</blue> (${
                                buildString setting@{
                                    setting.x1?.let { append("$it, ") }
                                    setting.x2?.let { append("$it, ") }
                                    setting.x3?.let { append("$it, ") }
                                    setting.rnd?.let { append("$it, ") }
                                    setting.invert?.let { append(it) }
                                }.removeSuffix(", ")
                            })</grey>\n"
                        )
                    }
                }
            }'>[2 Rules]</hover></blue>"
)