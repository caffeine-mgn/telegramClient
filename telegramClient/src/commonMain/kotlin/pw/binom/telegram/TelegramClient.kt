package pw.binom.telegram

import pw.binom.telegram.dto.*

interface TelegramClient {
    suspend fun deleteWebhook()
    suspend fun getWebhook(): WebhookInfo?
    suspend fun getUpdate(
        limit: Long? = 1000,
        timeout: Long = 60,
        allowedUpdates: List<EventType>? = null,
    ): List<Update>

    suspend fun deleteMessage(chatId: String, messageId: Long)
    suspend fun editMessage(message: EditTextRequest): Message?
    suspend fun setWebhook(request: SetWebhookRequest)
    suspend fun sendMessage(message: TextMessage): Message
    suspend fun answerCallbackQuery(query: AnswerCallbackQueryRequest)
    suspend fun setMyCommands(commands: List<BotCommand>)
    suspend fun getMyCommands(): List<BotCommand>
    suspend fun getMe(): User
}