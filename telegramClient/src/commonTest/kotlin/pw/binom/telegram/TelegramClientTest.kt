package pw.binom.telegram

import kotlinx.coroutines.test.runTest
import pw.binom.System
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.create
import pw.binom.io.use
import pw.binom.thread.Thread
import pw.binom.uuid.nextUuid
import kotlin.random.Random
import kotlin.test.Test

class TelegramClientTest {
    @Test
    fun test() = runTest {
        try {
            HttpClient.create().use { client ->
                val client = TelegramClient.open(httpClient = client, token = Random.nextUuid().toString())
                repeat(10) {
                    runCatching {
                        println("---===CALL===---")
                        client.getUpdate()
                    }
                }
            }
        } finally {
            repeat(10) {
                Thread.sleep(1000)
                System.gc()
                Thread.sleep(4000)
            }
            Thread.sleep(10000)
        }
    }
}
