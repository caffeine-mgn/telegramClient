plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.binom.publish) apply false
}
allprojects {
    if (version == "unspecified") {
        version = "1.0.0-SNAPSHOT"
    }
    group = "pw.binom"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.binom.pw")
    }
}
