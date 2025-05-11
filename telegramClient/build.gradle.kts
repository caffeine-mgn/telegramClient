import pw.binom.publish.allTargets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.binom.publish)
    id("maven-publish")
}

kotlin {
    allTargets {
        -"js"
        -"wasmJs"
        -"wasmWasi"
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api(libs.binom.io.http.client)
                api(libs.kotlinx.serialization.core)
                api(libs.kotlinx.serialization.json)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
/*
        val linuxX64Main by getting {
            dependencies {
                dependsOn(commonMain)
            }
        }

        val macosX64Main by getting {
            dependencies {
                dependsOn(linuxX64Main)
            }
        }
        val mingwX64Main by getting {
            dependencies {
                dependsOn(linuxX64Main)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib"))
            }
        }
*/
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

//apply<pw.binom.publish.plugins.PrepareProject>()

extensions.getByType(pw.binom.publish.plugins.PublicationPomInfoExtension::class).apply {
    useApache2License()
    gitScm("https://github.com/caffeine-mgn/telegramClient")
    author(
        id = "subochev",
        name = "Anton Subochev",
        email = "caffeine.mgn@gmail.com"
    )
}
