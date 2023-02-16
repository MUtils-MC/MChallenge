
plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}



tasks {
    shadowJar {
        dependencies {
            include {
                val moduleName = properties["module_name"]
                val split = it.moduleGroup.split('.')
                val prefix = "${split.getOrNull(0)}.${split.getOrNull(1)}"
                val isAPI = split.lastOrNull() == "api"
                val isModuleAPI = moduleName == split.getOrNull(split.size - 2)
//                println("Group:${it.moduleGroup} Prefix: $prefix - Module:$moduleName (${split.getOrNull(split.size - 2)}) - ShouldShade:${!isAPI || isModuleAPI}")
                prefix == "de.miraculixx" && (!isAPI || isModuleAPI)
            }
        }
    }
}