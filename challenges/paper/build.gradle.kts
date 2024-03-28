
plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

repositories {
    mavenLocal()
}

dependencies {
    implementation(project(":vanilla"))
    implementation(project(":core-paper"))
    implementation(project(":kpaper-light"))

    implementation(project(":challenges:api"))
    implementation(project(":world-creator:api"))
    implementation(project(":timer:api"))

    compileOnly("de.miraculixx:mweb:1.1.0")
    implementation("de.miraculixx:mbridge:1.0.0")
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/challenges/data/")
    }
}

group = "de.miraculixx.challenges"
setProperty("module_name", "challenges")