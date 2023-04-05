plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}


tasks {
    shadowJar {
        dependencies {
            val moduleName = properties["module_name"]
            include(dependency("dev.jorel:commandapi-shade"))
            include(dependency("dev.jorel:commandapi-kotlin"))
            include(dependency("io.ktor::"))
            include {
                val split = it.moduleGroup.split('.')
                val prefix = "${split.getOrNull(0)}.${split.getOrNull(1)}"
                val isAPI = split.lastOrNull() == "api"
                val isModuleAPI = moduleName == split.getOrNull(split.size - 2)
//                println("Group:${it.moduleGroup} Prefix: $prefix - Module:$moduleName (${split.getOrNull(split.size - 2)}) - ShouldShade:${!isAPI || isModuleAPI}")
                prefix == "de.miraculixx" && (!isAPI || isModuleAPI)
            }
        }
        val moduleName = properties["module_name"]
        relocate("de.miraculixx.mcore", "de.miraculixx.$moduleName.core")
        relocate("de.miraculixx.mvanilla", "de.miraculixx.$moduleName.vanilla")
        relocate("de.miraculixx.kpaper", "de.miraculixx.$moduleName.kpaper")
        relocate("de.miraculixx.mbridge", "de.miraculixx.$moduleName.bridge")
        relocate("io.ktor", "de.miraculixx.$moduleName.ktor")
        relocate("dev.jorel.commandapi", "de.miraculixx.$moduleName.command")
    }
}