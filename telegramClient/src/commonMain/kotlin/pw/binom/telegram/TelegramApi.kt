package pw.binom.telegram

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import pw.binom.URL
import pw.binom.io.*
import pw.binom.io.http.Headers
import pw.binom.io.httpClient.AsyncHttpClient
import pw.binom.io.socket.nio.SocketNIOManager
import pw.binom.telegram.dto.*

private val jsonSerialization = Json {
    ignoreUnknownKeys = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    encodeDefaults = false
}
private const val BASE_PATH = "https://api.telegram.org/bot"

class TelegramApi(var lastUpdate: Long, val token: String, manager: SocketNIOManager) {
    private val client = AsyncHttpClient(manager)
    private val baseUrl = URL("https://api.telegram.org/bot${UTF8.urlEncode(token)}")

    companion object {
        fun parseUpdate(json: String) =
                jsonSerialization.decodeFromJsonElement(ListSerializer(Update.serializer()), jsonSerialization.parseToJsonElement(json))

        suspend fun getWebhook(client: AsyncHttpClient, token: String): WebhookInfo {
            val url = URL("$BASE_PATH${UTF8.urlEncode(token)}/getWebhookInfo")
            val json = client.request("GET", url)
                    .response()
                    .utf8Reader().use {
                        it.readText()
                    }
            return jsonSerialization.decodeFromJsonElement(WebhookInfo.serializer(), getResult(json)!!)
        }

        suspend fun getUpdate(client: AsyncHttpClient, token: String, lastUpdate: Long): Pair<Long, List<Update>> {
            val url = URL("$BASE_PATH${UTF8.urlEncode(token)}/getUpdates?offset=${lastUpdate + 1}&timeout=${60}")
            val json = client.request("GET", url)
                    .response()
                    .utf8Reader().use {
                        it.readText()
                    }
            val resultJsonTree = getResult(json)
            val updates = jsonSerialization.decodeFromJsonElement(ListSerializer(Update.serializer()), resultJsonTree!!.jsonArray)
            val updateId = updates.lastOrNull()?.updateId
            return (updateId ?: 0L) to updates
        }

        suspend fun deleteWebhook(client: AsyncHttpClient, token: String) {
            val url = URL("$BASE_PATH${UTF8.urlEncode(token)}/deleteWebhook")
            val response = client.request("POST", url)
                    .addHeader(Headers.CONTENT_TYPE, "application/json")
                    .response()
            getResult(response.utf8Reader().use { it.readText() })
        }

        suspend fun setWebhook(client: AsyncHttpClient, token: String, request: SetWebhookRequest) {
            val url = URL("$BASE_PATH${UTF8.urlEncode(token)}/setWebhook")
            val response = client.request("POST", url)
                    .addHeader(Headers.CONTENT_TYPE, "application/json")
                    .upload().also {
                        it.utf8Appendable().append(jsonSerialization.encodeToString(SetWebhookRequest.serializer(), request))
                        Unit
                    }.response()
            getResult(response.utf8Reader().use { it.readText() })
        }

        suspend fun sendMessage(client: AsyncHttpClient, token: String, message: TextMessage): Message {
            val url = URL("$BASE_PATH${UTF8.urlEncode(token)}/sendMessage")
            val response = client.request("POST", url)
                    .addHeader(Headers.CONTENT_TYPE, "application/json")
                    .upload().also {
                        it.utf8Appendable().append(jsonSerialization.encodeToString(TextMessage.serializer(), message))
                        Unit
                    }.response()
            val responseText = response.utf8Reader().use { it.readText() }
            return jsonSerialization.decodeFromJsonElement(Message.serializer(), getResult(responseText)!!)
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

    suspend fun getUpdate(): List<Update> {
        val r = getUpdate(client, token, lastUpdate)
        lastUpdate = r.first
        return r.second
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