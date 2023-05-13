plugins {
    `kotlin-script`
    `maven-publish`
    `adventure-script`
    signing
}

group = "de.miraculixx.challenges.api"
setProperty("module_name", "challenges")

val githubRepo = "MiraculixxT/MUtils"
val isSnapshot = false

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    register("release") {
        group = "publishing"
        dependsOn("publish")
    }
}

publishing {
    repositories {
        maven {
            name = "ossrh"
            credentials(PasswordCredentials::class)
            setUrl(
                if (!isSnapshot) "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                else "https://s01.oss.sonatype.org/content/repositories/snapshots"
            )
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "de.miraculixx"
            artifactId = "challenge-api"
            version = "1.2.1"

            from(components["java"])

            pom {
                name.set("MUtils-Challenge-API")
                description.set("Access MUtils-Challenge through this API")
                url.set("https://mutils.net")

                developers {
                    developer {
                        id.set("miraculixx")
                        name.set("Miraculixx")
                        email.set("miraculixxyt@gmail.com")
                    }
                }

                licenses {
                    license {
                        name.set("GNU Affero General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.en.html")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/${githubRepo}.git")
                    url.set("https://github.com/${githubRepo}/tree/master/timer")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}
