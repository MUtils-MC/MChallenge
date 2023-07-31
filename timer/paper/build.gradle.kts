plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
    `adventure-script`
}

dependencies {
    implementation(project(":vanilla")) // Main Utils
    implementation(project(":core-paper")) // Paper specified Utils
    implementation(project(":kpaper-light"))
    implementation(project(":timer:api"))
    implementation(project(":timer:global"))

    implementation(project(":challenges:api")) // Internal API
//    implementation(project(":bridge")) // Internal API
}

group = "de.miraculixx.timer"
setProperty("module_name", "timer")

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/timer/data/")
    }
}