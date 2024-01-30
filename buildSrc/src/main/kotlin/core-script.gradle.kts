
plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.7"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("org.yaml:snakeyaml:2.2")
}