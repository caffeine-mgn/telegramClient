package pw.binom.telegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import pw.binom.http.client.HttpClientRunnable
import pw.binom.io.http.*
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.setHeader
import pw.binom.io.useAsync
import pw.binom.telegram.dto.*
import pw.binom.url.Query
import pw.binom.url.toURL

private val jsonSerialization = Json {
    ignoreUnknownKeys = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    encodeDefaults = false
    classDiscriminator = "@class"
}

private val BASE_PATH = "https://api.telegram.org/bot".toURL()
private val JSON_MIME_TYPE = "application/json;charset=utf-8"

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
            method = "GET",
            token = token,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = WebhookInfo.serializer().nullable,
            function = "getWebhookInfo",
            query = null,
        )

    suspend fun getUpdate(
        client: HttpClientRunnable,
        token: String,
        updateRequest: UpdateRequest,
    ): Pair<Long, List<Update>> {
        val updates = send(
            client = client,
            method = "POST",
            token = token,
            requestSerializer = UpdateRequest.serializer(),
            request = updateRequest,
            responseSerializer = ListSerializer(Update.serializer()),
            function = "deleteWebhook",
            query = null,
        )
        val updateId = updates.lastOrNull()?.updateId
        return (updateId ?: 0L) to updates
    }

    suspend fun deleteWebhook(client: HttpClientRunnable, token: String) {
        send(
            client = client,
            method = "POST",
            token = token,
            requestSerializer = Unit.serializer(),
            request = Unit,
            responseSerializer = Unit.serializer(),
            function = "deleteWebhook",
            query = null,
        )
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
        var url = BASE_PATH
            .appendPath("bot$token", direction = false, encode = true)
            .appendPath(function)
        if (query != null) {
            url = url.copy(query = query)
        }
        val req = client.request(
            method = method,
            url = url,
        )
        req.headers.contentType = JSON_MIME_TYPE
        req.headers.httpContentLength = HttpContentLength.CHUNKED
        val responseText = req.connect().useAsync { connection ->
            if (requestSerializer != Unit.serializer()) {
                val requestJson = jsonSerialization.encodeToString(requestSerializer, request)
                connection.sendText(requestJson)
            }
            require(connection.getResponseCode() == 200) { "Response code is ${connection.getResponseCode()}" }
            connection.readAllText()
        }
        if (responseSerializer === Unit.serializer()) {
            return Unit as RESPOSNE
        }
        val resp = getResult(responseText)
        if (resp == null) {
            if (responseSerializer.descriptor.isNullable) {
                return null as RESPOSNE
            }
            throw IllegalStateException("Returns unexpected null")
        }
        return jsonSerialization.decodeFromJsonElement(responseSerializer, resp)
    }

    suspend fun setWebhook(client: HttpClientRunnable, token: String, request: SetWebhookRequest) {
        send(
            client = client,
            token = token,
            requestSerializer = SetWebhookRequest.serializer(),
            responseSerializer = Unit.serializer(),
            request = request,
            function = "setWebhook",
            method = "POST",
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
            method = "POST",
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
            method = "POST",
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
            method = "GET",
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
            method = "POST",
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
            method = "POST",
            query = null,
        )

    suspend fun deleteMessage(client: HttpClientRunnable, token: String, chatId: String, messageId: Long) {
        send(
            client = client,
            method = "POST",
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
            method = "GET",
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
