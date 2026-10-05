package pw.binom.telegram

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.utils.io.ByteReadChannel
import kotlinx.io.Sink
import pw.binom.telegram.dto.*
import kotlin.time.Duration

const val DEFAULT_BOT_API_URL: String = "https://api.telegram.org"

interface TelegramClient {
    companion object {
        fun open(
            engineFactory: HttpClientEngineFactory<*>,
            token: String,
            lastUpdate: Long = 0,
            baseUrl: String = DEFAULT_BOT_API_URL,
        ): TelegramClient = TelegramClientImpl(
            httpClient = HttpClient(engineFactory),
            token = token,
            lastUpdate = lastUpdate,
            baseUrl = baseUrl,
        )
    }

    val baseUrl: String

    suspend fun deleteWebhook()
    suspend fun getWebhook(): WebhookInfo

    /**
     * @param timeout Timeout in seconds (Telegram accepts values 0-90)
     */
    suspend fun getUpdate(
        limit: Long? = 100,
        timeout: Long = 60,
        allowedUpdates: List<EventType>? = null,
    ): List<Update>

    suspend fun deleteMessage(chatId: String, messageId: Long)
    suspend fun editMessage(message: EditTextRequest): EditMessageResult
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
        contentType: String = "audio/mpeg",
        data: Sink.() -> Unit,
    ): Message

    suspend fun getFile(
        fileId: String,
    ): File

    suspend fun downloadFile(
        filePath: String,
    ): ByteReadChannel

    suspend fun downloadFileById(
        fileId: String,
    ): ByteReadChannel

    suspend fun sendChatAction(
        chatId: String,
        event: SendChatEvent.Action,
        businessConnectionId: String? = null,
        messageThreadId: String? = null,
    )
}