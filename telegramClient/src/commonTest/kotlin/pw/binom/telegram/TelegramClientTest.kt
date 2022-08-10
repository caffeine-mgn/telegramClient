package pw.binom.telegram

import kotlinx.coroutines.test.runTest
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.create
import pw.binom.io.use
import pw.binom.nextUuid
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test

class TelegramClientTest {
    @Ignore
    @Test
    fun test() = runTest {
        HttpClient.create().use { client ->
            val client = TelegramClient.open(httpClient = client, token = Random.nextUuid().toString())
            client.getUpdate()
        }
    }
}
