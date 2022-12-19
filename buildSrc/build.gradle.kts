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
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

val kotlinVersion = project.property("kotlin.version") as String
val httpClientVersion = project.property("http_client.version") as String
val kotlinxCoroutinesVersion = project.property("kotlinx_coroutines.version") as String
val kotlinxSerializationVersion = project.property("kotlinx_serialization.version") as String

buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_VERSION", "\"$kotlinVersion\"")
    buildConfigField("String", "KOTLINX_COROUTINES_VERSION", "\"$kotlinxCoroutinesVersion\"")
    buildConfigField("String", "KOTLINX_SERIALIZATION_VERSION", "\"$kotlinxSerializationVersion\"")
    buildConfigField("String", "HTTP_CLIENT_VERSION", "\"$httpClientVersion\"")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.binom.pw")
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    api("pw.binom:binom-publish:0.1.2")
    api("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
}