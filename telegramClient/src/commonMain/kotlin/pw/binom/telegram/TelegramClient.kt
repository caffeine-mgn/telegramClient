package pw.binom.telegram

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.io.Sink
import kotlinx.serialization.json.Json
import pw.binom.telegram.dto.*
import kotlin.time.Duration

/**
 * Public client for the Telegram Bot HTTP API.
 *
 * Engine selection is the caller's responsibility — pass an [HttpClientEngineFactory]
 * (e.g. `OkHttp`, `Apache`, `CIO`, `Darwin`/`Js` for the platform you deploy on)
 * to [open], or construct your own [HttpClient] and hand it to [wrap].
 */
interface TelegramClient : AutoCloseable {

    suspend fun deleteWebhook(): Boolean
    suspend fun getWebhook(): WebhookInfo
    suspend fun setWebhook(request: SetWebhookRequest): Boolean

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

    suspend fun sendMessage(message: TextMessage): Message
    suspend fun deleteMessage(chatId: String, messageId: Long): Boolean
    suspend fun editMessage(message: EditTextRequest): EditMessageResult

    suspend fun getMe(): User

    suspend fun getFile(fileId: String): File
    suspend fun downloadFile(filePath: String): ByteReadChannel
    suspend fun downloadFileById(fileId: String): ByteReadChannel

    suspend fun sendChatAction(
        chatId: String,
        action: SendChatEvent.Action,
        messageThreadId: String? = null,
    ): Boolean

    suspend fun setMyCommands(commands: List<BotCommand>): Boolean
    suspend fun getMyCommands(): List<BotCommand>

    suspend fun getUpdate(
        offset: Long? = null,
        limit: Int? = null,
        timeout: Int? = null,
        allowedUpdates: List<String>? = null,
    ): List<Update>

    companion object {
        /**
         * Build a client that owns its Ktor [HttpClient]; the caller picks the engine factory.
         *
         * Example:
         * ```
         * TelegramClient.open(CIO, token = "...")
         * TelegramClient.open(OkHttp, token = "...") { install(HttpTimeout) { ... } }
         * ```
         */
        fun <T : HttpClientEngineFactory<*>> open(
            engineFactory: T,
            token: String,
            baseUrl: String = "https://api.telegram.org",
            configure: HttpClientConfig<*>.() -> Unit = {},
        ): TelegramClient {
            val http = HttpClient(engineFactory) {
                configure()
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        classDiscriminator = "@class"
                        explicitNulls = false
                        encodeDefaults = true
                    })
                }
            }
            return TalkToClient(http, token, baseUrl)
        }

        /**
         * Wrap an existing Ktor [HttpClient] that already has its engine and ContentNegotiation set up.
         */
        fun wrap(
            http: HttpClient,
            token: String,
            baseUrl: String = "https://api.telegram.org",
        ): TelegramClient = TalkToClient(http, token, baseUrl)
    }
}

/**
 * Wraps a user-supplied [HttpClient]; the engine and config stay with the caller.
 */
class TalkToClient internal constructor(
    private val http: HttpClient,
    private val token: String,
    private val baseUrl: String,
) : TelegramClient {

    override suspend fun deleteWebhook(): Boolean =
        TelegramApi.deleteWebhook(http, token, baseUrl)

    override suspend fun getWebhook(): WebhookInfo =
        TelegramApi.getWebhook(http, token, baseUrl)

    override suspend fun setWebhook(request: SetWebhookRequest): Boolean =
        TelegramApi.setWebhook(http, token, request, baseUrl)

    override suspend fun sendVoice(
        chatId: String,
        caption: String?,
        duration: Duration?,
        disableNotification: Boolean?,
        messageThreadId: String?,
        parseMode: ParseMode?,
        contentType: String,
        data: Sink.() -> Unit,
    ): Message = TelegramApi.sendVoice(
        client = http,
        token = token,
        chatId = chatId,
        caption = caption,
        duration = duration,
        disableNotification = disableNotification,
        messageThreadId = messageThreadId,
        parseMode = parseMode,
        contentType = contentType,
        baseUrl = baseUrl,
        data = data,
    )

    override suspend fun sendMessage(message: TextMessage): Message =
        TelegramApi.sendMessage(http, token, message, baseUrl)

    override suspend fun deleteMessage(chatId: String, messageId: Long): Boolean =
        TelegramApi.deleteMessage(http, token, chatId, messageId, baseUrl)

    override suspend fun editMessage(message: EditTextRequest): EditMessageResult =
        TelegramApi.editMessage(http, token, message, baseUrl)

    override suspend fun getMe(): User =
        TelegramApi.getMe(http, token, baseUrl)

    override suspend fun getFile(fileId: String): File =
        TelegramApi.getFile(http, token, fileId, baseUrl)

    override suspend fun downloadFile(filePath: String): ByteReadChannel =
        TelegramApi.downloadFile(http, token, filePath, baseUrl)

    override suspend fun downloadFileById(fileId: String): ByteReadChannel =
        TelegramApi.downloadFileById(http, token, fileId, baseUrl)

    override suspend fun sendChatAction(
        chatId: String,
        action: SendChatEvent.Action,
        messageThreadId: String?,
    ): Boolean = TelegramApi.sendChatAction(
        client = http,
        token = token,
        data = SendChatEvent(chatId = chatId, action = action, messageThreadId = messageThreadId),
        baseUrl = baseUrl,
    )

    override suspend fun setMyCommands(commands: List<BotCommand>): Boolean =
        TelegramApi.setMyCommands(http, token, commands, baseUrl)

    override suspend fun getMyCommands(): List<BotCommand> =
        TelegramApi.getMyCommands(http, token, baseUrl)

    override suspend fun getUpdate(
        offset: Long?,
        limit: Int?,
        timeout: Int?,
        allowedUpdates: List<String>?,
    ): List<Update> = TelegramApi.getUpdate(
        client = http,
        token = token,
        offset = offset,
        limit = limit,
        timeout = timeout,
        allowedUpdates = allowedUpdates,
        baseUrl = baseUrl,
    )

    override fun close() {
        http.close()
    }
}