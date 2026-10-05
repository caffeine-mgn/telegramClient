package pw.binom.telegram

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.Charsets
import kotlinx.io.Sink
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import pw.binom.telegram.dto.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private val jsonSerialization = Json {
    ignoreUnknownKeys = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    encodeDefaults = false
    classDiscriminator = "@class"
}

private const val DEFAULT_BASE_URL = "https://api.telegram.org"
private val JSON_MIME_TYPE = ContentType.Application.Json.withCharset(Charsets.UTF_8)
private val DEFAULT_BOUNDARY = "-----------telegramClientBoundary"

private fun functionPath(baseUrl: String, token: String, function: String): String =
    "$baseUrl/bot$token/$function"

private fun filePath(baseUrl: String, token: String, filePath: String): String =
    "$baseUrl/file/bot$token/$filePath"

@OptIn(ExperimentalTime::class)
object TelegramApi {

    const val BOT_API_SECRET_TOKEN_HEADER = "X-Telegram-Bot-Api-Secret-Token"

    fun parseUpdate(json: String): Update =
        jsonSerialization.decodeFromJsonElement(
            Update.serializer(),
            jsonSerialization.parseToJsonElement(json),
        )

    suspend fun getWebhook(
        client: HttpClient,
        token: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): WebhookInfo =
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = WebhookInfo.serializer(),
            function = "getWebhookInfo",
            method = HttpMethod.Get,
        )

    suspend fun sendChatAction(
        client: HttpClient,
        token: String,
        data: SendChatEvent,
        baseUrl: String = DEFAULT_BASE_URL,
    ) {
        send(
            function = "sendChatAction",
            method = HttpMethod.Post,
            requestSerializer = SendChatEvent.serializer(),
            request = data,
            responseSerializer = Unit.serializer(),
            client = client,
            token = token,
            baseUrl = baseUrl,
        )
    }

    suspend fun getUpdate(
        client: HttpClient,
        token: String,
        updateRequest: UpdateRequest,
        baseUrl: String = DEFAULT_BASE_URL,
    ): Pair<Long, List<Update>> {
        val updates = send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = UpdateRequest.serializer(),
            request = updateRequest,
            responseSerializer = ListSerializer(Update.serializer()),
            function = "getUpdates",
            method = HttpMethod.Get,
        )
        val updateId = updates.lastOrNull()?.updateId
        return (updateId ?: 0L) to updates
    }

    suspend fun deleteWebhook(
        client: HttpClient,
        token: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ) {
        send(
            client = client,
            method = HttpMethod.Post,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = Unit.serializer(),
            function = "deleteWebhook",
        )
    }

    suspend fun downloadFile(
        client: HttpClient,
        token: String,
        filePath: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): ByteReadChannel {
        val response = client.get(filePath(baseUrl, token, filePath))
        check(response.status == HttpStatusCode.OK) { "Invalid response code ${response.status}" }
        return response.bodyAsChannel()
    }

    suspend fun getFile(
        client: HttpClient,
        token: String,
        fileId: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): File = send(
        client = client,
        method = HttpMethod.Get,
        token = token,
        baseUrl = baseUrl,
        requestSerializer = Unit.serializer(),
        responseSerializer = File.serializer(),
        request = Unit,
        function = "getFile",
        parameters = Parameters.build {
            append("file_id", fileId)
        },
    )

    @OptIn(ExperimentalContracts::class)
    @Suppress("LEAKED_IN_PLACE_LAMBDA")
    suspend fun sendVoice(
        client: HttpClient,
        token: String,
        chatId: String,
        caption: String? = null,
        duration: Duration? = null,
        disableNotification: Boolean? = null,
        messageThreadId: String? = null,
        parseMode: ParseMode? = null,
        contentType: String = "audio/mpeg",
        baseUrl: String = DEFAULT_BASE_URL,
        data: Sink.() -> Unit,
    ): Message {
        contract {
            callsInPlace(data, InvocationKind.AT_MOST_ONCE)
        }
        val voiceHeaders = Headers.build {
            append(HttpHeaders.ContentType, contentType)
            append(HttpHeaders.ContentDisposition, "filename=\"audio.mp3\"")
        }
        val body = MultiPartFormDataContent(
            formData {
                append("chat_id", chatId)
                caption?.let { append("caption", it) }
                duration?.let { append("duration", it.inWholeSeconds.toString()) }
                disableNotification?.let { append("disable_notification", it.toString()) }
                messageThreadId?.let { append("message_thread_id", it) }
                parseMode?.let { append("parse_mode", it.code) }
                append(key = "voice", headers = voiceHeaders, bodyBuilder = data)
            },
            boundary = DEFAULT_BOUNDARY,
        )
        val response = client.post(functionPath(baseUrl, token, "sendVoice")) {
            setBody(body)
        }
        val text = response.bodyAsText()
        check(response.status == HttpStatusCode.OK) {
            "Invalid response code ${response.status}\nResponse: $text"
        }
        val result = getResult(text)
        return jsonSerialization.decodeFromJsonElement(Message.serializer(), result)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <REQUEST, RESPONSE> send(
        client: HttpClient,
        method: HttpMethod,
        token: String,
        baseUrl: String,
        requestSerializer: KSerializer<REQUEST>,
        responseSerializer: KSerializer<RESPONSE>,
        request: REQUEST,
        function: String,
        parameters: Parameters? = null,
    ): RESPONSE {
        val response = client.request(functionPath(baseUrl, token, function)) {
            this.method = method
            if (parameters != null) {
                url { this.parameters.appendAll(parameters) }
            }
            if (requestSerializer !== Unit.serializer()) {
                contentType(JSON_MIME_TYPE)
                setBody(jsonSerialization.encodeToString(requestSerializer, request))
            }
        }
        val responseCode = response.status.value
        val text = response.bodyAsText()
        require(responseCode == 200) {
            "Response code is $responseCode.\nRequest: ${if (requestSerializer !== Unit.serializer()) jsonSerialization.encodeToString(requestSerializer, request) else "<no body>"}\nResponse: $text"
        }
        if (responseSerializer === Unit.serializer()) {
            return Unit as RESPONSE
        }
        val resp = getResult(text)
        return try {
            jsonSerialization.decodeFromJsonElement(responseSerializer, resp)
        } catch (e: SerializationException) {
            throw IllegalStateException(
                "Can't decode response\nSerializer: ${responseSerializer.descriptor.serialName}\njson: $resp",
                e,
            )
        }
    }

    suspend fun setWebhook(
        client: HttpClient,
        token: String,
        request: SetWebhookRequest,
        baseUrl: String = DEFAULT_BASE_URL,
    ) {
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = SetWebhookRequest.serializer(),
            responseSerializer = Unit.serializer(),
            request = request,
            function = "setWebhook",
            method = HttpMethod.Post,
        )
    }

    suspend fun answerCallbackQuery(
        client: HttpClient,
        token: String,
        query: AnswerCallbackQueryRequest,
        baseUrl: String = DEFAULT_BASE_URL,
    ) {
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = AnswerCallbackQueryRequest.serializer(),
            request = query,
            responseSerializer = Unit.serializer(),
            function = "answerCallbackQuery",
            method = HttpMethod.Post,
        )
    }

    suspend fun setMyCommands(
        client: HttpClient,
        token: String,
        commands: List<BotCommand>,
        baseUrl: String = DEFAULT_BASE_URL,
    ) {
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = SetMyCommandsRequest.serializer(),
            request = SetMyCommandsRequest(commands),
            responseSerializer = Unit.serializer(),
            function = "setMyCommands",
            method = HttpMethod.Post,
        )
    }

    suspend fun getMyCommands(
        client: HttpClient,
        token: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): List<BotCommand> =
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = Unit.serializer(),
            responseSerializer = ListSerializer(BotCommand.serializer()),
            request = Unit,
            function = "getMyCommands",
            method = HttpMethod.Get,
        )

    suspend fun editMessage(
        client: HttpClient,
        token: String,
        message: EditTextRequest,
        baseUrl: String = DEFAULT_BASE_URL,
    ): EditMessageResult =
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = EditTextRequest.serializer(),
            responseSerializer = EditMessageResult.serializer(),
            request = message,
            function = "editMessageText",
            method = HttpMethod.Post,
        )

    suspend fun sendMessage(
        client: HttpClient,
        token: String,
        message: TextMessage,
        baseUrl: String = DEFAULT_BASE_URL,
    ): Message =
        send(
            client = client,
            token = token,
            baseUrl = baseUrl,
            requestSerializer = TextMessage.serializer(),
            request = message,
            responseSerializer = Message.serializer(),
            function = "sendMessage",
            method = HttpMethod.Post,
        )

    suspend fun deleteMessage(
        client: HttpClient,
        token: String,
        chatId: String,
        messageId: Long,
        baseUrl: String = DEFAULT_BASE_URL,
    ) {
        send(
            client = client,
            method = HttpMethod.Post,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = Unit.serializer(),
            function = "deleteMessage",
            token = token,
            baseUrl = baseUrl,
            parameters = Parameters.build {
                append("chat_id", chatId)
                append("message_id", messageId.toString())
            },
        )
    }

    suspend fun getMe(
        client: HttpClient,
        token: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): User =
        send(
            client = client,
            method = HttpMethod.Get,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = User.serializer(),
            function = "getMe",
            token = token,
            baseUrl = baseUrl,
        )

    private fun getResult(json: String): JsonElement {
        val tree = jsonSerialization.parseToJsonElement(json).jsonObject
        if (tree["ok"]?.jsonPrimitive?.boolean != true) {
            val code = tree["error_code"]?.jsonPrimitive?.int ?: 0
            throw TelegramException(
                code = code,
                description = tree["description"]?.jsonPrimitive?.content ?: "Unknown Error",
            )
        }
        return tree["result"] ?: JsonNull
    }
}