# TelegramClient для Kotlin Multiplatform

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)
[![Ktor 3.2.0](https://img.shields.io/badge/Ktor-3.2.0-blue.svg?style=flat&logo=kotlin)](https://ktor.io)

Kotlin Multiplatform клиент Telegram Bot API поверх
[Ktor HttpClient](https://ktor.io/docs/client.html). Публикуется в **Maven
Central** как `pw.binom.telegram:telegramClient`.

> [English version](README.md)

## Подключение

```kotlin
dependencies {
    implementation("pw.binom.telegram:telegramClient:0.1.0")
    implementation("io.ktor:ktor-client-cio:3.2.0")
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