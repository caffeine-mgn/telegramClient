package pw.binom.telegram

import io.ktor.client.HttpClient
import io.ktor.utils.io.ByteReadChannel
import kotlinx.io.Sink
import pw.binom.telegram.dto.*
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration

@OptIn(ExperimentalAtomicApi::class)
class TelegramClientImpl internal constructor(
    private val httpClient: HttpClient,
    val token: String,
    override val baseUrl: String = DEFAULT_BOT_API_URL,
    lastUpdate: Long = 0,
) : TelegramClient {

    constructor(
        httpClient: HttpClient,
        token: String,
        lastUpdate: Long = 0,
    ) : this(httpClient, token, DEFAULT_BOT_API_URL, lastUpdate)

    private val waitingUpdate = AtomicBoolean(false)
    private val updateRequest = UpdateRequest(offset = lastUpdate, limit = null, timeout = 60, null)

    override suspend fun getUpdate(
        limit: Long?,
        timeout: Long,
        allowedUpdates: List<EventType>?,
    ): List<Update> {
        check(waitingUpdate.compareAndSet(false, true)) { "You are already waiting for updates" }
        try {
            updateRequest.limit = limit
            updateRequest.timeout = timeout
            updateRequest.allowedUpdates = allowedUpdates
            val r = TelegramApi.getUpdate(httpClient, token, updateRequest, baseUrl)
            updateRequest.offset = r.first + 1
            return r.second
        } finally {
            waitingUpdate.store(false)
        }
    }

    override suspend fun getWebhook(): WebhookInfo =
        TelegramApi.getWebhook(httpClient, token, baseUrl)

    override suspend fun deleteWebhook() =
        TelegramApi.deleteWebhook(httpClient, token, baseUrl)

    override suspend fun deleteMessage(chatId: String, messageId: Long) =
        TelegramApi.deleteMessage(httpClient, token, chatId, messageId, baseUrl)

    override suspend fun editMessage(message: EditTextRequest): EditMessageResult =
        TelegramApi.editMessage(httpClient, token, message, baseUrl)

    override suspend fun setWebhook(request: SetWebhookRequest) =
        TelegramApi.setWebhook(httpClient, token, request, baseUrl)

    override suspend fun sendMessage(message: TextMessage): Message =
        TelegramApi.sendMessage(httpClient, token, message, baseUrl)

    override suspend fun answerCallbackQuery(query: AnswerCallbackQueryRequest) =
        TelegramApi.answerCallbackQuery(httpClient, token, query, baseUrl)

    override suspend fun setMyCommands(commands: List<BotCommand>) =
        TelegramApi.setMyCommands(httpClient, token, commands, baseUrl)

    override suspend fun getMyCommands(): List<BotCommand> =
        TelegramApi.getMyCommands(httpClient, token, baseUrl)

    override suspend fun getMe(): User =
        TelegramApi.getMe(httpClient, token, baseUrl)

    @Suppress("LEAKED_IN_PLACE_LAMBDA")
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
        client = httpClient,
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

    override suspend fun getFile(fileId: String): File =
        TelegramApi.getFile(httpClient, token, fileId, baseUrl)

    override suspend fun downloadFile(filePath: String): ByteReadChannel =
        TelegramApi.downloadFile(httpClient, token, filePath, baseUrl)

    override suspend fun downloadFileById(fileId: String): ByteReadChannel {
        val file = getFile(fileId)
        val path = file.filePath
            ?: error("Telegram returned no file_path for fileId=$fileId")
        return downloadFile(path)
    }

    override suspend fun sendChatAction(
        chatId: String,
        event: SendChatEvent.Action,
        businessConnectionId: String?,
        messageThreadId: String?,
    ) {
        TelegramApi.sendChatAction(
            client = httpClient,
            token = token,
            data = SendChatEvent(
                chatId = chatId,
                action = event,
                messageThreadId = messageThreadId,
                businessConnectionId = businessConnectionId,
            ),
            baseUrl = baseUrl,
        )
    }
}