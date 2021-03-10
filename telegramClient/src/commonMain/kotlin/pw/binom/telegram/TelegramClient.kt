package pw.binom.telegram

import pw.binom.io.IOException
import pw.binom.telegram.dto.*

interface TelegramClient {
    suspend fun deleteWebhook()
    suspend fun getWebhook(): WebhookInfo?
    suspend fun getUpdate(): List<Update>
    suspend fun setWebhook(request: SetWebhookRequest)
    suspend fun sendMessage(message: TextMessage): Message
}