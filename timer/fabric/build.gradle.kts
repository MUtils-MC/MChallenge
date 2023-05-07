plugins {
    `kotlin-script`
    `core-script`
    `fabric-script`
    `adventure-script`
}

dependencies {
    implementation(include(project(":vanilla"))!!)
    implementation(include(project(":core-fabric", "namedElements"))!!)
    implementation(include(project(":timer:global"))!!)
    implementation(include(project(":timer:api"))!!)
    implementation(project(":challenges:api")) // Internal API
}

loom {
    runs {
        named("server") {
            ideConfigGenerated(true)
        }
        named("client") {
            ideConfigGenerated(true)
        }
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("net.silkmc.silk.core.annotations.ExperimentalSilkApi")
        }
    }
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/timer/data/")
    }
}

group = "de.miraculixx.timer"
setProperty("module_name", "timer")
