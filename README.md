# TelegramClient for Kotlin Multiplatform

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)
[![Ktor 3.2.0](https://img.shields.io/badge/Ktor-3.2.0-blue.svg?style=flat&logo=kotlin)](https://ktor.io)

Kotlin Multiplatform Telegram Bot API client on top of the
[Ktor HttpClient](https://ktor.io/docs/client.html). Published to **Maven
Central** as `pw.binom.telegram:telegramClient`.

> [Русская версия](README.ru.md)

## Install

```kotlin
dependencies {
    implementation("pw.binom.telegram:telegramClient:0.1.0")
    implementation("io.ktor:ktor-client-cio:3.2.0")
}
```

## Use

```kotlin
val tg = TelegramClient.open(CIO, token = "<bot-token>")
tg.use {
    it.sendMessage(TextMessage(chatId = "1", text = "Hello, world!"))
}
```

`TelegramClient` is `AutoCloseable`. If you already maintain your own
`HttpClient`, hand it to `TelegramClient.wrap(http, token = "…")` instead.

## Targets

JVM, iOS (arm64 + simulator), macOS (arm64), Linux (x64 + arm64), Windows
(mingw x64).

## Release

Tag `N.N.N`, push — GitHub Actions (`.github/workflows/release.yml`) builds,
runs tests, signs with the team's GPG key and publishes to Maven Central via
[Vanniktech's `maven-publish`](https://github.com/vanniktech/maven-publish)
plugin.