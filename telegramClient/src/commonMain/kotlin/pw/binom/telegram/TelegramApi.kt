package pw.binom.telegram

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import pw.binom.io.IOException
import pw.binom.io.UTF8
import pw.binom.io.http.HTTPMethod
import pw.binom.io.http.Headers
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.addHeader
import pw.binom.io.httpClient.setHeader
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

internal const val BASE_PATH = "https://api.telegram.org/bot"
internal const val BASE_PATH2 = "http://192.168.73.12:8080/bot"

object TelegramApi {

    fun parseUpdate(json: String) =
        jsonSerialization.decodeFromJsonElement(
            ListSerializer(Update.serializer()),
            jsonSerialization.parseToJsonElement(json)
        )

    suspend fun getWebhook(client: HttpClient, token: String): WebhookInfo? {
        val url = "$BASE_PATH${UTF8.urlEncode(token)}/getWebhookInfo".toURIOrNull()!!
        val json = client.request(HTTPMethod.GET, url)
            .getResponse()
            .readText().use {
                it.readText()
            }
        return jsonSerialization.decodeFromJsonElement(WebhookInfo.serializer(), getResult(json)!!)
    }

    suspend fun getUpdate(client: HttpClient, token: String, updateRequest: UpdateRequest): Pair<Long, List<Update>> {
        val url =
            "$BASE_PATH${UTF8.urlEncode(token)}/getUpdates".toURIOrNull()!!
        val json = client.request(HTTPMethod.POST, url)
            .setHeader(Headers.CONTENT_TYPE, "application/json;charset=utf-8")
            .writeText {
                it.append(jsonSerialization.encodeToString(UpdateRequest.serializer(), updateRequest))
            }
            .readText().use {
                it.readText()
            }
        val resultJsonTree = getResult(json)
        val updates =
            jsonSerialization.decodeFromJsonElement(ListSerializer(Update.serializer()), resultJsonTree!!.jsonArray)
        val updateId = updates.lastOrNull()?.updateId
        println("${updateRequest.offset} -> $updateId")
        return (updateId ?: 0L) to updates
    }

    suspend fun deleteWebhook(client: HttpClient, token: String) {
        val url = "$BASE_PATH${UTF8.urlEncode(token)}/deleteWebhook".toURIOrNull()!!
        val response = client.request(HTTPMethod.POST, url)
            .getResponse()
            .readText().use {
                it.readText()
            }
        getResult(response)
    }

    suspend fun setWebhook(client: HttpClient, token: String, request: SetWebhookRequest) {
        val url = "$BASE_PATH${UTF8.urlEncode(token)}/setWebhook".toURIOrNull()!!
        val response = client.request(HTTPMethod.POST, url)
            .setHeader(Headers.CONTENT_TYPE, "application/json;charset=utf-8")
            .writeText {
                it.append(jsonSerialization.encodeToString(SetWebhookRequest.serializer(), request))
            }
            .readText().use { it.readText() }
        getResult(response)
    }

    suspend fun sendMessage(client: HttpClient, token: String, message: TextMessage): Message {
        val url = "$BASE_PATH${UTF8.urlEncode(token)}/sendMessage".toURIOrNull()!!
        val response = client.request(HTTPMethod.POST, url)
            .setHeader(Headers.CONTENT_TYPE, "application/json;charset=utf-8")
            .writeText {
                it.append(jsonSerialization.encodeToString(TextMessage.serializer(), message))
            }
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