pluginManagement {
    repositories {
        gradlePluginPortal()
        //maven("https://plugins.gradle.org/m2")
        mavenCentral()

        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        //maven("https://dl.bintray.com/kotlin/kotlin-dev") {
        //    mavenContent { snapshotsOnly() }
        //}
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/plugins-release")

        // detekt
        //jcenter()

        // Snapshots von Spring Framework, Spring Data, Spring Security und Spring Cloud
        //maven("https://repo.spring.io/libs-snapshot")
    }
}

buildCache {
    local {
        directory = "C:/Z/caches"
    }
}

rootProject.name = "gesundheitsamt"
