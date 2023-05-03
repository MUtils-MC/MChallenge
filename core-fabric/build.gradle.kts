plugins {
    `kotlin-script`
    `core-script`
    `fabric-script`
    `adventure-script`
    `shadow-script`
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(project(":vanilla"))
}