plugins {
    `kotlin-script`
    `core-script`
    `fabric-script`
    `adventure-script`
}

dependencies {
    implementation(include(project(":vanilla"))!!)
    implementation(include(project(":core-fabric", "namedElements"))!!)
    implementation(include(project(":challenges:global"))!!)
    implementation(include(project(":challenges:api"))!!)
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

group = "de.miraculixx.challenge"
setProperty("module_name", "timer")
