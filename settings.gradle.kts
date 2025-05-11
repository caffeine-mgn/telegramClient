pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://repo.binom.pw")
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "TelegramClient"
include(":telegramClient")