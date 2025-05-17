package pw.binom.telegram

import pw.binom.http.client.HttpClientRunnable
import pw.binom.io.AsyncInput
import pw.binom.io.AsyncOutput
import pw.binom.io.httpClient.HttpClient
import pw.binom.telegram.dto.*
import kotlin.time.Duration

interface TelegramClient {
    companion object {
        fun open(httpClient: HttpClientRunnable, token: String, lastUpdate: Long = 0) = TelegramClientImpl(
            lastUpdate = lastUpdate,
            token = token,
            client = httpClient
        )
    }

    suspend fun deleteWebhook()
    suspend fun getWebhook(): WebhookInfo?

    /**
     * @param timeout Timeout in seconds
     */
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
    suspend fun sendVoice(
        chatId: String,
        caption: String? = null,
        duration: Duration? = null,
        disableNotification: Boolean? = null,
        messageThreadId: String? = null,
        parseMode: ParseMode? = null,
        contentType: String = "application/octet-stream",
        data: suspend (AsyncOutput) -> Unit,
    ): Message

    suspend fun getFile(
        fileId: String,
    ): File

    suspend fun downloadFile(
        filePath: String,
    ): AsyncInput

    suspend fun sendChatAction(
        chatId: String,
        event: SendChatEvent.Action,
        businessConnectionId: String? = null,
        messageThreadId: String? = null,
    )
}
