import pw.binom.Versions

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-serialization")
    id("maven-publish")
}

kotlin {
    linuxX64()
    if (pw.binom.Target.LINUX_ARM32HFP_SUPPORT) {
        linuxArm32Hfp()
    }
    mingwX64()
    if (pw.binom.Target.MINGW_X86_SUPPORT) {
        mingwX86()
    }
    macosX64()
    jvm()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.KOTLIN_VERSION}")
                api("pw.binom.io:httpClient:${Versions.HTTP_VERSION}")
//                api ("pw.binom.io:core:$network_version")
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.KOTLINX_SERIALIZATION_VERSION}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLINX_SERIALIZATION_VERSION}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.KOTLINX_COROUTINES_VERSION}")
            }
        }

        val linuxX64Main by getting {
            dependencies {
                dependsOn(commonMain)
            }
        }
        if (pw.binom.Target.LINUX_ARM32HFP_SUPPORT) {
            val linuxArm32HfpMain by getting {
                dependencies {
                    dependsOn(linuxX64Main)
                }
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
        if (pw.binom.Target.MINGW_X86_SUPPORT) {
            val mingwX86Main by getting {
                dependencies {
                    dependsOn(mingwX64Main)
                }
            }
        }

        val jvmMain by getting {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN_VERSION}")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

apply<pw.binom.publish.plugins.PrepareProject>()

extensions.getByType(pw.binom.publish.plugins.PublicationPomInfoExtension::class).apply {
    useApache2License()
    gitScm("https://github.com/caffeine-mgn/telegramClient")
    author(
        id = "subochev",
        name = "Anton Subochev",
        email = "caffeine.mgn@gmail.com"
    )
}
