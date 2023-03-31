plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}



tasks {
    shadowJar {
        dependencies {
            include(dependency("dev.jorel:commandapi-shade"))
            include(dependency("dev.jorel:commandapi-kotlin"))
            include {
                if (it.moduleGroup.startsWith("dev.jorel")) return@include true

                val moduleName = properties["module_name"]
                val split = it.moduleGroup.split('.')
                val prefix = "${split.getOrNull(0)}.${split.getOrNull(1)}"
                val isAPI = split.lastOrNull() == "api"
                val isModuleAPI = moduleName == split.getOrNull(split.size - 2)
//                println("Group:${it.moduleGroup} Prefix: $prefix - Module:$moduleName (${split.getOrNull(split.size - 2)}) - ShouldShade:${!isAPI || isModuleAPI}")
                prefix == "de.miraculixx" && (!isAPI || isModuleAPI)
            }
        }
        relocate("dev.jorel.commandapi", "de.miraculixx.commandapi")
    }
}