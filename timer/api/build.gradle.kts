plugins {
    `kotlin-script`
    `maven-publish`
    signing
}

group = "de.miraculixx.timer.api"
setProperty("module_name", "timer")

val githubRepo = "MiraculixxT/MUtils"
val isSnapshot = false

//publishing {
//    repositories {
//        maven {
//            name = "ossrh"
//            credentials(PasswordCredentials::class)
//            setUrl(
//                if (!isSnapshot) "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
//                else "https://s01.oss.sonatype.org/content/repositories/snapshots"
//            )
//        }
//    }
//
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = "de.miraculixx.mutils"
//            artifactId = "timer-api"
//            version = "1.1.0"
//
//            from(components["java"])
//
//            pom {
//                name.set("MUtils-Timer-API")
//                description.set("Access the MUtils-Timer through this API")
//                url.set("https://mutils.de")
//
//                developers {
//                    developer {
//                        id.set("miraculixx")
//                        name.set("Miraculixx")
//                        email.set("miraculixxyt@gmail.com")
//                    }
//                }
//
//                licenses {
//                    license {
//                        name.set("GNU General Public License v3.0")
//                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
//                    }
//                }
//
//                scm {
//                    connection.set("scm:git:git://github.com/${githubRepo}.git")
//                    url.set("https://github.com/${githubRepo}/tree/master/timer")
//                }
//            }
//        }
//    }
//}
//
//signing {
//    sign(publishing.publications)
//}
