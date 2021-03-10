package pw.binom.telegram

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import pw.binom.io.IOException
import pw.binom.io.UTF8
import pw.binom.io.http.HTTPMethod
import pw.binom.io.http.Headers
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.addHeader
import pw.binom.io.readText
import pw.binom.io.use
import pw.binom.telegram.dto.*
import pw.binom.toURIOrNull

private val jsonSerialization = Json {
    ignoreUnknownKeys = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    encodeDefaults = false
}
private const val BASE_PATH = "https://api.telegram.org/bot"

class TelegramApi(var lastUpdate: Long = 0, val token: String, val client: HttpClient) {

    private val baseUrl = "$BASE_PATH${UTF8.urlEncode(token)}".toURIOrNull()!!

    companion object {
        fun parseUpdate(json: String) =
            jsonSerialization.decodeFromJsonElement(
                ListSerializer(Update.serializer()),
                jsonSerialization.parseToJsonElement(json)
            )

        suspend fun getWebhook(client: HttpClient, token: String): WebhookInfo {
            val url = "$BASE_PATH${UTF8.urlEncode(token)}/getWebhookInfo".toURIOrNull()!!
            val json = client.request(HTTPMethod.GET, url)
                .getResponse()
                .readText().use {
                    it.readText()
                }
            return jsonSerialization.decodeFromJsonElement(WebhookInfo.serializer(), getResult(json)!!)
        }

        suspend fun getUpdate(client: HttpClient, token: String, lastUpdate: Long): Pair<Long, List<Update>> {
            val url =
                "$BASE_PATH${UTF8.urlEncode(token)}/getUpdates?offset=${lastUpdate + 1}&timeout=${60}".toURIOrNull()!!
            val json = client.request(HTTPMethod.GET, url)
                .getResponse()
                .readText().use {
                    it.readText()
                }
            val resultJsonTree = getResult(json)
            val updates =
                jsonSerialization.decodeFromJsonElement(ListSerializer(Update.serializer()), resultJsonTree!!.jsonArray)
            val updateId = updates.lastOrNull()?.updateId
            return (updateId ?: 0L) to updates
        }

        suspend fun deleteWebhook(client: HttpClient, token: String) {
            val url = "$BASE_PATH${UTF8.urlEncode(token)}/deleteWebhook".toURIOrNull()!!
            val response = client.request(HTTPMethod.POST, url)
                .addHeader(Headers.CONTENT_TYPE, "application/json")
                .getResponse()
                .readText().use {
                    it.readText()
                }
            getResult(response)
        }

        suspend fun setWebhook(client: HttpClient, token: String, request: SetWebhookRequest) {
            val url = "$BASE_PATH${UTF8.urlEncode(token)}/setWebhook".toURIOrNull()!!
            val response = client.request(HTTPMethod.POST, url)
                .addHeader(Headers.CONTENT_TYPE, "application/json")
                .writeText().also {
                    it.append(jsonSerialization.encodeToString(SetWebhookRequest.serializer(), request))
                }
                .getResponse()
                .readText().use { it.readText() }
            getResult(response)
        }

        suspend fun sendMessage(client: HttpClient, token: String, message: TextMessage): Message {
            val url = "$BASE_PATH${UTF8.urlEncode(token)}/sendMessage".toURIOrNull()!!
            val response = client.request(HTTPMethod.POST, url)
                .addHeader(Headers.CONTENT_TYPE, "application/json")
                .writeText().also {
                    it.append(jsonSerialization.encodeToString(TextMessage.serializer(), message))
                }
                .getResponse()
                .readText().use {
                    it.readText()
                }
            return jsonSerialization.decodeFromJsonElement(Message.serializer(), getResult(response)!!)
        }

        suspend fun deleteMessage(client: HttpClient, token: String, chatId: String, messageId: Long): Message {
            val url =
                "$BASE_PATH${UTF8.urlEncode(token)}/deleteMessage?chat_id=${UTF8.encode(chatId)}&message_id=$messageId"
                    .toURIOrNull()!!
            val response = client.request(HTTPMethod.POST, url)
                .addHeader(Headers.CONTENT_TYPE, "application/json")
                .getResponse()
                .readText().use {
                    it.readText()
                }
            return jsonSerialization.decodeFromJsonElement(Message.serializer(), getResult(response)!!)
        }

        private fun getResult(json: String): JsonElement? {
            val tree = jsonSerialization.parseToJsonElement(json).jsonObject
            if (tree["ok"]?.jsonPrimitive?.boolean != true) {
                val code = tree["error_code"]?.jsonPrimitive?.int ?: 0
                throw when (code) {
                    else -> TelegramException(
                        code = code,
                        description = tree["description"]?.jsonPrimitive?.content ?: "Unknown Error"
                    )
                }
            }
            return tree["result"]
        }
    }

    suspend fun sendMessage(message: TextMessage): Message =
        sendMessage(client, token, message)

    suspend fun setWebhook(request: SetWebhookRequest) {
        setWebhook(client, token, request)
    }

    suspend fun deleteWebhook() {
        deleteWebhook(client, token)
    }

    suspend fun getWebhook(): WebhookInfo = getWebhook(client, token)

    private var watingUpdate = false

    suspend fun getUpdate(): List<Update> {
        check(watingUpdate) { "You already waiting messages" }
        try {
            watingUpdate = true
            val r = getUpdate(client, token, lastUpdate)
            lastUpdate = r.first
            return r.second
        } finally {
            watingUpdate = false
        }
    }

    abstract class AbstractTelegramException : IOException {
        constructor() : super()
        constructor(message: String?) : super(message)
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(cause: Throwable?) : super(cause)
    }

    class InvalidRequestException : AbstractTelegramException {
        constructor() : super()
        constructor(message: String?) : super(message)
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(cause: Throwable?) : super(cause)
    }

    class TelegramException(val code: Int, val description: String) : AbstractTelegramException() {
        override val message: String?
            get() = "$code: $description"
    }
}