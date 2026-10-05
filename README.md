# TelegramClient for Kotlin Multiplatform

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)
[![Ktor 3.2.0](https://img.shields.io/badge/Ktor-3.2.0-blue.svg?style=flat&logo=kotlin)](https://ktor.io)

Kotlin Multiplatform Telegram Bot API client built on top of the
[Ktor HttpClient](https://ktor.io/docs/client.html).

The HTTP engine is **not bundled** with the artifact — bring whichever engine
factory suits your target (`CIO`, `OkHttp`, `Darwin`, `Js`, …). This keeps the
library lean and lets the consumer pick the runtime.

## Install

`pw.binom.telegram:telegramClient:<version>` is published to **Maven Central**
through [Vanniktech's `maven-publish`](https://github.com/vanniktech/maven-publish)
plugin. Add the dependency to your multiplatform module:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("pw.binom.telegram:telegramClient:<version>")
        }
    }
}
```

The matching Ktor engine artifact (e.g. `io.ktor:ktor-client-cio` or
`io.ktor:ktor-client-okhttp`) is **yours** to pick — declare it in the
platform source set where you'll run.

## Use

```kotlin
val client = TelegramClient.open(
    engineFactory = CIO,
    token = "<bot-token>",
)

client.sendMessage(TextMessage(chatId = "1", text = "Hello, world!"))
```

### Passing a pre-built `HttpClient`

If you already maintain a Ktor client with custom plugins (auth, logging,
timeouts…), just hand it to `TelegramClient.wrap`:

```kotlin
val http = HttpClient(CIO) {
    install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
}
val tg = TelegramClient.wrap(http, token = "<bot-token>")
```

### Streaming uploads

`sink.voice` takes a `kotlinx.io.Sink.() -> Unit` so the bytes never have to
live in memory as a single `ByteArray`:

```kotlin
client.sendVoice(
    chatId = "1",
    caption = "voice clip",
    duration = 3.seconds,
) {
    write(opaqueBytes)
}
```

## Targets: JVM, iOS (arm64 + simulator), macOS (arm64), Linux (x64 + arm64),
Windows (mingw x64). Native code is compiled and verified per release;
native tests run only on the matching host (macOS for `iosSimulatorArm64` /
`macosArm64`, Windows for `mingwX64`).

## Build

```bash
./gradlew jvmTest                    # JVM unit tests
./gradlew publishToMavenLocal        # smoke-test the Maven Central coordinates locally
```

CI/CD lives in [`.github/workflows/release.yml`](.github/workflows/release.yml) —
it cuts a release on tag push and publishes to Maven Central via
[Vanniktech's plugin](https://github.com/vanniktech/maven-publish).