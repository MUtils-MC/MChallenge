
plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
    implementation(project(":core-paper")) // Paper specified Utils
    implementation(project(":kpaper-light"))

    implementation(project(":challenges:api")) // Internal API
    implementation(project(":challenges:global"))
    implementation(project(":world-creator:api")) // External API
    implementation(project(":bridge")) // External API
    implementation(project(":timer:api")) // External API

    compileOnly("de.miraculixx:mweb:1.1.0")
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/challenges/data/")
    }
}

group = "de.miraculixx.challenges"
setProperty("module_name", "challenges")