package pw.binom.telegram

import pw.binom.io.UTF8
import pw.binom.io.httpClient.HttpClient
import pw.binom.neverFreeze
import pw.binom.telegram.dto.*

class TelegramClientImpl(var lastUpdate: Long = 0, val token: String, val client: HttpClient) : TelegramClient {

    private var watingUpdate = false

    override suspend fun getUpdate(): List<Update> {
        check(!watingUpdate) { "You already waiting messages" }
        try {
            watingUpdate = true
            val r = TelegramApi.getUpdate(client, token, UpdateRequest(offset = lastUpdate, limit = null, 60, null))
            lastUpdate = r.first + 1
            println(":lastUpdate -> $lastUpdate")
            return r.second
        } finally {
            watingUpdate = false
        }
    }

    override suspend fun getWebhook() = TelegramApi.getWebhook(client, token)

    override suspend fun deleteWebhook() {
        TelegramApi.deleteWebhook(client, token)
    }

    override suspend fun setWebhook(request: SetWebhookRequest) {
        TelegramApi.setWebhook(client, token, request)
    }

    override suspend fun sendMessage(message: TextMessage): Message =
        TelegramApi.sendMessage(client, token, message)

    init {
        neverFreeze()
    }
}