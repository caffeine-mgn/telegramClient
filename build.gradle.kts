//buildscript {
//    ext.kotlin_version = "1.5.31"
//    ext.network_version = "0.1.31"
//    ext.telegram_version = "0.1.31"
//    ext.junit_version = '4.12'
//
//    repositories {
//        mavenLocal()
//        mavenCentral()
//        jcenter()
//        maven {
//            url 'https://plugins.gradle.org/m2/'
//        }
//        maven(url="http://repo.binom.pw")
//    }
//    dependencies {
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
//        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
//    }
//}

allprojects {
    version = pw.binom.Versions.LIB_VERSION
    group = "pw.binom"

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven(url="https://repo.binom.pw")
    }
}