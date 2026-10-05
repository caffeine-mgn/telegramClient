import org.gradle.plugins.signing.SigningExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.vanniktech.maven.publish)
}

val releaseVersion: String =
    (project.findProperty("version") as String?)
        ?: (System.getenv("GITHUB_REF_NAME")?.takeIf { it.matches(Regex("[0-9]+\\.[0-9]+\\.[0-9]+.*")) })
        ?: "0.1.0-SNAPSHOT"

version = releaseVersion

kotlin {
    jvmToolchain(21)
    jvm()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.client.core)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.kotlinx.serialization.core)
                api(libs.kotlinx.serialization.json)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.client.mock)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

group = "pw.binom.telegram"

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = "pw.binom.telegram",
        artifactId = "telegramClient",
        version = project.version.toString(),
    )

    pom {
        name.set("TelegramClient")
        description.set("Kotlin Multiplatform Telegram Bot API client over Ktor HttpClient.")
        url.set("https://github.com/caffeine-mgn/telegramClient")
        inceptionYear.set("2026")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("subochev")
                name.set("Anton Subochev")
                email.set("caffeine.mgn@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/caffeine-mgn/telegramClient.git")
            developerConnection.set("scm:git:ssh://git@github.com/caffeine-mgn/telegramClient.git")
            url.set("https://github.com/caffeine-mgn/telegramClient")
        }
    }
}

pluginManager.withPlugin("signing") {
    if (findProperty("signingUseGpg") == "true") {
        extensions.configure<SigningExtension>("signing") {
            useGpgCmd()
        }
        logger.lifecycle("[signing] Using system gpg via signing.gnupg.keyName=${findProperty("signing.gnupg.keyName")}")
        return@withPlugin
    }
    logger.lifecycle("[signing] No in-memory PGP key configured; publications will be signed by the publishing plugin only.")
}