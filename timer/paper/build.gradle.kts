plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

repositories {
    mavenLocal()
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
    implementation(project(":core-paper")) // Paper specified Utils
    implementation(project(":kpaper-light"))
    implementation(project(":timer:api"))
    implementation(project(":timer:global"))

    implementation(project(":challenges:api")) // Internal API
    implementation("de.miraculixx:mbridge:1.0.0")
}

group = "de.miraculixx.timer"
setProperty("module_name", "timer")

sourceSets {
    main {
        resources.srcDirs("$rootDir/timer/data/")
    }
}