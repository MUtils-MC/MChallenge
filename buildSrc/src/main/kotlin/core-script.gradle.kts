
plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.1.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("org.yaml:snakeyaml:1.33")
}