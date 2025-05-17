package pw.binom.telegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import pw.binom.http.client.Http11ClientExchange
import pw.binom.http.client.HttpClientRunnable
import pw.binom.io.AsyncInput
import pw.binom.io.AsyncOutput
import pw.binom.io.http.*
import pw.binom.io.useAsync
import pw.binom.telegram.dto.*
import pw.binom.telegram.utils.AutoClosableAsyncInput
import pw.binom.url.Query
import pw.binom.url.URL
import pw.binom.url.toURL
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

private val jsonSerialization = Json {
    ignoreUnknownKeys = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    encodeDefaults = false
    classDiscriminator = "@class"
}

private val BASE_PATH = "https://api.telegram.org/".toURL()
private val BASE_BOT_PATH = BASE_PATH.appendPath("bot")//"https://api.telegram.org/bot".toURL()
private val JSON_MIME_TYPE = "application/json;charset=utf-8"
private const val METHOD_POST = "POST"
private const val METHOD_GET = "GET"

@OptIn(kotlin.time.ExperimentalTime::class)
object TelegramApi {

    fun parseUpdate(json: String) =
        jsonSerialization.decodeFromJsonElement(
            Update.serializer(),
            jsonSerialization.parseToJsonElement(json)
        )

    suspend fun getWebhook(client: HttpClientRunnable, token: String): WebhookInfo? =
        send(
            client = client,
            method = METHOD_GET,
            token = token,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = WebhookInfo.serializer().nullable,
            function = "getWebhookInfo",
            query = null,
        )

    suspend fun sendChatAction(
        client: HttpClientRunnable,
        token: String, data: SendChatEvent,
    ) {
        send(
            function = "sendChatAction",
            method = METHOD_POST,
            requestSerializer = SendChatEvent.serializer(),
            request = data,
            responseSerializer = Unit.serializer(),
            client = client,
            token = token,
            query = null,
        )
    }

    suspend fun getUpdate(
        client: HttpClientRunnable,
        token: String,
        updateRequest: UpdateRequest,
    ): Pair<Long, List<Update>> {
        val updates = send(
            client = client,
            method = METHOD_GET,
            token = token,
            requestSerializer = UpdateRequest.serializer(),
            request = updateRequest,
            responseSerializer = ListSerializer(Update.serializer()),
            function = "getUpdates",
            query = null,
        )
        val updateId = updates.lastOrNull()?.updateId
        return (updateId ?: 0L) to updates
    }

    suspend fun deleteWebhook(client: HttpClientRunnable, token: String) {
        send(
            client = client,
            method = METHOD_POST,
            token = token,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = Unit.serializer(),
            function = "deleteWebhook",
            query = null,
        )
    }

    suspend fun downloadFile(
        client: HttpClientRunnable,
        token: String,
        filePath: String,
    ): AsyncInput {
        val resultUrl=BASE_PATH.appendPath("/file/bot$token/$filePath")
        val request = client.request(
            method = METHOD_GET,
            url = resultUrl,
        ).connect() as Http11ClientExchange
        return AutoClosableAsyncInput(request.getInput()) {
            request.asyncCloseAnyway()
        }
    }

    suspend fun getFile(
        client: HttpClientRunnable,
        token: String,
        fileId: String,
    ) = send(
        client = client,
        method = METHOD_GET,
        token = token,
        requestSerializer = Unit.serializer(),
        responseSerializer = File.serializer(),
        request = Unit,
        function = "getFile",
        query = Query.new("file_id", fileId),
    )

    @OptIn(ExperimentalContracts::class)
    suspend fun sendVoice(
        client: HttpClientRunnable,
        token: String,
        chatId: String,
        caption: String? = null,
        duration: Duration? = null,
        disableNotification: Boolean? = null,
        messageThreadId: String? = null,
        parseMode: ParseMode? = null,
        contentType: String = "application/octet-stream",
        data: suspend (AsyncOutput) -> Unit,
    ): Message {
        contract {
            callsInPlace(data, InvocationKind.EXACTLY_ONCE)
        }
        var q = Query.new("chat_id", chatId)
        if (caption != null) {
            q = q.append("caption", caption)
        }
        if (duration != null) {
            q = q.append("duration", duration.inWholeSeconds.toString())
        }
        if (disableNotification != null) {
            q = q.append("disable_notification", disableNotification.toString())
        }
        if (disableNotification != null) {
            q = q.append("message_thread_id", messageThreadId)
        }
        if (parseMode != null) {
            q = q.append("parse_mode", parseMode.code)
        }
        val url = buildUrl(
            function = "sendVoice",
            query = q,
            token = token
        )
        val boundary = AsyncMultipartOutput.generateBoundary()
        client.request(method = METHOD_POST, url = url).also {
            it.headers.httpContentLength = HttpContentLength.CHUNKED
            it.headers.contentType = "multipart/form-data; boundary=$boundary"
        }.connect().useAsync { ex ->
            ex as Http11ClientExchange
            AsyncMultipartOutput(boundary = boundary, stream = ex.getOutput()).useAsync { multipart ->
                val headers = HashHeaders2()
                headers.contentType = contentType
                multipart.formData("voice", headers = headers, fileName = "audio.mp3")
                data(multipart)
            }
            val responseContent = ex.readAllText()
            val r = getResult(responseContent)!!
            check(ex.getResponseCode() == 200) { "Invalid response code ${ex.getResponseCode()}" }
            return jsonSerialization.decodeFromString(Message.serializer(), responseContent)
        }

    }

    private fun buildUrl(
        function: String,
        query: Query?,
        token: String,
    ): URL {
//        var url = (BASE_PATH.toString()+token).toURL()
        var url = BASE_BOT_PATH
            .appendPath("$token", direction = false, encode = false)
            .appendPath(function)
        if (query != null) {
            url = url.copy(query = query)
        }
        return url
    }

    private suspend fun <REQUEST, RESPOSNE> send(
        client: HttpClientRunnable,
        method: String,
        token: String,
        requestSerializer: KSerializer<REQUEST>,
        responseSerializer: KSerializer<RESPOSNE>,
        request: REQUEST,
        function: String,
        query: Query?,
    ): RESPOSNE {
        val url = buildUrl(
            function = function,
            query = query,
            token = token
        )
        val req = client.request(
            method = method,
            url = url,
        )
        if (requestSerializer != Unit.serializer()) {
            req.headers.contentType = JSON_MIME_TYPE
        }
        req.headers.keepAlive = false
        req.headers.httpContentLength = if (requestSerializer != Unit.serializer()) {
            HttpContentLength.CHUNKED
        } else {
            HttpContentLength.NONE
        }
        val responseText = req.connect().useAsync { connection ->
            val requestJson = if (requestSerializer != Unit.serializer()) {
                jsonSerialization.encodeToString(requestSerializer, request)
            } else {
                null
            }
            if (requestJson != null) {
                connection.sendText(requestJson)
            }
            val txt = connection.readAllText()
            val bytes =
                requestJson?.encodeToByteArray()?.mapIndexed { index, it -> "$index: $it ${it.toInt().toChar()}" }
                    ?.joinToString("\n")
            require(connection.getResponseCode() == 200) { "Response code is ${connection.getResponseCode()}.\nRequest: $requestJson\nbytes: $bytes\nResponse: $txt" }
            txt
        }
        val resp = getResult(responseText)
        if (responseSerializer === Unit.serializer()) {
            return Unit as RESPOSNE
        }
        if (resp == null) {
            if (responseSerializer.descriptor.isNullable) {
                return null as RESPOSNE
            }
            throw IllegalStateException("Returns unexpected null")
        }
        try {
            return jsonSerialization.decodeFromJsonElement(responseSerializer, resp)
        } catch (e: SerializationException) {
            throw IllegalStateException("Can't decode response\nSerializer: ${responseSerializer.descriptor.serialName}\njson: $resp")
        }
    }

    suspend fun setWebhook(client: HttpClientRunnable, token: String, request: SetWebhookRequest) {
        send(
            client = client,
            token = token,
            requestSerializer = SetWebhookRequest.serializer(),
            responseSerializer = Unit.serializer(),
            request = request,
            function = "setWebhook",
            method = METHOD_POST,
            query = null,
        )
    }

    suspend fun answerCallbackQuery(client: HttpClientRunnable, token: String, query: AnswerCallbackQueryRequest) {
        send(
            client = client,
            token = token,
            requestSerializer = AnswerCallbackQueryRequest.serializer(),
            request = query,
            responseSerializer = Unit.serializer(),
            function = "answerCallbackQuery",
            method = METHOD_POST,
            query = null,
        )
    }

    suspend fun setMyCommands(client: HttpClientRunnable, token: String, commands: List<BotCommand>) {
        send(
            client = client,
            token = token,
            requestSerializer = SetMyCommandsRequest.serializer(),
            request = SetMyCommandsRequest(commands),
            responseSerializer = Unit.serializer(),
            function = "setMyCommands",
            method = METHOD_POST,
            query = null,
        )
    }

    suspend fun getMyCommands(client: HttpClientRunnable, token: String) =
        send(
            client = client,
            token = token,
            requestSerializer = Unit.serializer(),
            responseSerializer = ListSerializer(BotCommand.serializer()),
            request = Unit,
            function = "getMyCommands",
            method = METHOD_GET,
            query = null,
        )

    suspend fun editMessage(client: HttpClientRunnable, token: String, message: EditTextRequest) =
        send(
            client = client,
            token = token,
            requestSerializer = EditTextRequest.serializer(),
            responseSerializer = Message.serializer().nullable,
            request = message,
            function = "editMessageText",
            method = METHOD_POST,
            query = null,
        )

    suspend fun sendMessage(client: HttpClientRunnable, token: String, message: TextMessage) =
        send(
            client = client,
            token = token,
            requestSerializer = TextMessage.serializer(),
            request = message,
            responseSerializer = Message.serializer(),
            function = "sendMessage",
            method = METHOD_POST,
            query = null,
        )

    suspend fun deleteMessage(client: HttpClientRunnable, token: String, chatId: String, messageId: Long) {
        send(
            client = client,
            method = METHOD_POST,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = User.serializer(),
            function = "deleteMessage",
            token = token,
            query = Query.new("chat_id", chatId).append("message_id", messageId.toString()),
        )
    }

    suspend fun getMe(client: HttpClientRunnable, token: String) =
        send(
            client = client,
            method = METHOD_GET,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = User.serializer(),
            function = "getMe",
            token = token,
            query = null,
        )

    private fun getResult(json: String): JsonElement? {
        val tree = jsonSerialization.parseToJsonElement(json).jsonObject
        if (tree["ok"]?.jsonPrimitive?.boolean != true) {
            val code = tree["error_code"]?.jsonPrimitive?.int ?: 0
            throw TelegramException(
                code = code,
                description = tree["description"]?.jsonPrimitive?.content ?: "Unknown Error"
            )
        }
        return tree["result"]
    }
}
