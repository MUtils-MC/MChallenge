repositories {
    mavenCentral()
}

allprojects {
    group = project.extra["maven_group"] as String
    description = project.extra["project_description"] as String
    version = project.extra["project_version"] as String
}