# TelegramClient for Kotlin Multiplatform
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)
[![Ktor 3.2.0](https://img.shields.io/badge/Ktor-3.2.0-blue.svg?style=flat&logo=kotlin)](https://ktor.io)
<br>
Kotlin Multiplatform Telegram Bot API client built on top of the
[Ktor HttpClient](https://ktor.io/docs/client.html). The HTTP engine is
**not** bundled — pass whichever engine factory you want
(`CIO`, `OkHttp`, `Darwin`, `Js`, …) when constructing the client:

```kotlin
val client = TelegramClient.open(
    engineFactory = CIO,
    token = "<bot-token>",
)
```

Targets: JVM, iOS (arm64 + simulator), macOS (arm64), Linux (x64 + arm64)
and Windows (mingw x64).