package pw.binom.telegram

import pw.binom.io.httpClient.HttpClient
import pw.binom.telegram.dto.*

class TelegramClientImpl(lastUpdate: Long = 0, val token: String, val client: HttpClient) : TelegramClient {

    private var watingUpdate = false
    private val updateRequest = UpdateRequest(offset = lastUpdate, limit = null, timeout = 60, null)

    override suspend fun getUpdate(
        limit: Long?,
        timeout: Long,
        allowedUpdates: List<EventType>?,
    ): List<Update> {
        check(!watingUpdate) { "You already waiting messages" }
        try {
            updateRequest.also {
                it.limit = limit
                it.timeout = timeout
                it.allowedUpdates = allowedUpdates
            }
            watingUpdate = true
            val r = TelegramApi.getUpdate(client, token, updateRequest)
            updateRequest.offset = r.first + 1
            return r.second
        } finally {
            watingUpdate = false
        }
    }

    override suspend fun deleteMessage(chatId: String, messageId: Long) =
        TelegramApi.deleteMessage(client = client, token = token, chatId = chatId, messageId = messageId)

    override suspend fun editMessage(message: EditTextRequest): Message? =
        TelegramApi.editMessage(client, token, message)

    override suspend fun getWebhook() = TelegramApi.getWebhook(client, token)

    override suspend fun deleteWebhook() {
        TelegramApi.deleteWebhook(client, token)
    }

    override suspend fun setWebhook(request: SetWebhookRequest) {
        TelegramApi.setWebhook(client, token, request)
    }

    override suspend fun sendMessage(message: TextMessage): Message =
        TelegramApi.sendMessage(client, token, message)

    override suspend fun answerCallbackQuery(query: AnswerCallbackQueryRequest) =
        TelegramApi.answerCallbackQuery(client, token, query)

    override suspend fun setMyCommands(commands: List<BotCommand>) =
        TelegramApi.setMyCommands(client, token, commands)

    override suspend fun getMyCommands(): List<BotCommand> =
        TelegramApi.getMyCommands(client, token)

    override suspend fun getMe(): User =
        TelegramApi.getMe(client, token)
}
