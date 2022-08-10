buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.binom.pw")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

plugins {
    kotlin("jvm") version "1.7.10"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.binom.pw")
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.7.10")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.6.0")
    api("pw.binom:binom-publish:0.1.2")
    api("org.jetbrains.kotlin:kotlin-serialization:1.6.20")
}