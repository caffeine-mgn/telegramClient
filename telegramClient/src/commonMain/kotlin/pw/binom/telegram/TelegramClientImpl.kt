package pw.binom.telegram

import pw.binom.http.client.HttpClientRunnable
import pw.binom.io.AsyncInput
import pw.binom.io.AsyncOutput
import pw.binom.telegram.dto.*
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

@OptIn(ExperimentalAtomicApi::class)
class TelegramClientImpl(lastUpdate: Long = 0, val token: String, val client: HttpClientRunnable) : TelegramClient {

    private var watingUpdate = AtomicBoolean(false)
    private val updateRequest = UpdateRequest(offset = lastUpdate, limit = null, timeout = 60, null)

    override suspend fun getUpdate(
        limit: Long?,
        timeout: Long,
        allowedUpdates: List<EventType>?,
    ): List<Update> {
        check(watingUpdate.compareAndSet(false, true)) { "You already waiting messages" }
        try {
            updateRequest.also {
                it.limit = limit
                it.timeout = timeout
                it.allowedUpdates = allowedUpdates
            }
            watingUpdate.store(true)
            val r = TelegramApi.getUpdate(client, token, updateRequest)
            updateRequest.offset = r.first + 1
            return r.second
        } finally {
            watingUpdate.store(false)
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

    override suspend fun getFile(
        fileId: String,
    ) = TelegramApi.getFile(client = client, token = token, fileId = fileId)

    override suspend fun downloadFile(
        filePath: String,
    ) = TelegramApi.downloadFile(
        client = client,
        token = token,
        filePath = filePath,
    )

    override suspend fun sendVoice(
        chatId: String,
        caption: String?,
        duration: Duration?,
        disableNotification: Boolean?,
        messageThreadId: String?,
        parseMode: ParseMode?,
        contentType: String,
        data: suspend (AsyncOutput) -> Unit,
    ) = TelegramApi.sendVoice(
        client = client,
        token = token,
        chatId = chatId,
        duration = duration,
        caption = caption,
        disableNotification = disableNotification,
        messageThreadId = messageThreadId,
        parseMode = parseMode,
        contentType = contentType,
        data = data
    )

    override suspend fun sendChatAction(
        chatId: String,
        action: SendChatEvent.Action,
        businessConnectionId: String?,
        messageThreadId: String?,
    ) = TelegramApi.sendChatAction(
        client = client, token = token, data = SendChatEvent(
            chatId = chatId,
            action = action,
            businessConnectionId = businessConnectionId,
            messageThreadId = messageThreadId,
        )
    )
}
