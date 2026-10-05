# TelegramClient для Kotlin Multiplatform

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)
[![Ktor 3.2.0](https://img.shields.io/badge/Ktor-3.2.0-blue.svg?style=flat&logo=kotlin)](https://ktor.io)

Kotlin Multiplatform клиент Telegram Bot API поверх
[Ktor HttpClient](https://ktor.io/docs/client.html). Публикуется в **Maven
Central** как `pw.binom.telegram:telegramClient`.

> 🇬🇧 [English version](README.md)

## Подключение

Подберите Ktor engine под каждую платформу — в самой библиотеке engine нет.

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("pw.binom.telegram:telegramClient:0.1.0")
        }

        jvmMain.dependencies   { implementation("io.ktor:ktor-client-cio:3.2.0") }
        iosMain.dependencies   { implementation("io.ktor:ktor-client-darwin:3.2.0") }
        macosMain.dependencies { implementation("io.ktor:ktor-client-darwin:3.2.0") }
        linuxMain.dependencies { implementation("io.ktor:ktor-client-cio:3.2.0") }
        mingwMain.dependencies { implementation("io.ktor:ktor-client-cio:3.2.0") }
        jsMain.dependencies    { implementation("io.ktor:ktor-client-js:3.2.0") }
    }
}
```

## Использование

```kotlin
val tg = TelegramClient.open(CIO, token = "<bot-token>")
tg.use {
    it.sendMessage(TextMessage(chatId = "1", text = "Привет, мир!"))
}
```

`TelegramClient` — `AutoCloseable`. Если у вас уже есть свой `HttpClient`,
передайте его в `TelegramClient.wrap(http, token = "…")`.

## Платформы

JVM, iOS (arm64 + simulator), macOS (arm64), Linux (x64 + arm64), Windows
(mingw x64).

## Релиз

Создайте тег `N.N.N`, запушьте — GitHub Actions (`.github/workflows/release.yml`)
соберёт проект, прогоняет тесты, подпишет артефакты GPG-ключом команды и
опубликует их в Maven Central через
[Vanniktech `maven-publish`](https://github.com/vanniktech/maven-publish).