package pw.binom.telegram

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import pw.binom.io.http.HTTPMethod
import pw.binom.io.http.Headers
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.setHeader
import pw.binom.io.readText
import pw.binom.io.use
import pw.binom.telegram.dto.*
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

    suspend fun getWebhook(client: HttpClient, token: String): WebhookInfo? {
        val url = BASE_PATH
            .appendPath(token, direction = false, encode = true)
            .appendPath("getWebhookInfo")
        val json = client.connect(HTTPMethod.GET.code, url)
            .getResponse()
            .readText().use {
                it.readText()
            }
        return jsonSerialization.decodeFromJsonElement(WebhookInfo.serializer(), getResult(json) ?: return null)
    }

    suspend fun getUpdate(
        client: HttpClient,
        token: String,
        updateRequest: UpdateRequest
    ): Pair<Long, List<Update>> {
        try {
            val url =
                BASE_PATH
                    .appendPath(token, direction = false, encode = true)
                    .appendPath("getUpdates")
            val json = client.connect(HTTPMethod.POST.code, url)
                .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
                .writeTextAndGetResponse {
                    it.append(jsonSerialization.encodeToString(UpdateRequest.serializer(), updateRequest))
                }
                .readText {
                    it.readText()
                }
            val resultJsonTree = getResult(json)
            val updates =
                jsonSerialization.decodeFromJsonElement(ListSerializer(Update.serializer()), resultJsonTree!!.jsonArray)
            val updateId = updates.lastOrNull()?.updateId
            return (updateId ?: 0L) to updates
        } catch (e: Throwable) {
            throw InvalidRequestException("Can't get Telegram Updates", e)
        }
    }

    suspend fun deleteWebhook(client: HttpClient, token: String) {
        val url =
            BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("deleteWebhook")
        val response = client.connect(HTTPMethod.POST.code, url)
            .getResponse()
            .readText().use {
                it.readText()
            }
        getResult(response)
    }

    suspend fun setWebhook(client: HttpClient, token: String, request: SetWebhookRequest) {
        val sendBody = jsonSerialization.encodeToString(SetWebhookRequest.serializer(), request)
        val url =
            BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("setWebhook")
        try {
            val response = client.connect(HTTPMethod.POST.code, url)
                .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
                .writeTextAndGetResponse {
                    it.append(sendBody)
                }
                .readText().use { it.readText() }
            getResult(response)
        } catch (e: Throwable) {
            throw InvalidRequestException("Sent \"$sendBody\"", e)
        }
    }

    suspend fun answerCallbackQuery(client: HttpClient, token: String, query: AnswerCallbackQueryRequest) {
        val sendBody = jsonSerialization.encodeToString(AnswerCallbackQueryRequest.serializer(), query)
        try {
            val url =
                BASE_PATH
                    .appendPath(token, direction = false, encode = true)
                    .appendPath("answerCallbackQuery")
            val response = client.connect(HTTPMethod.POST.code, url)
                .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
                .writeTextAndGetResponse {
                    it.append(sendBody)
                }
                .readText().use {
                    it.readText()
                }
            getResult(response)
        } catch (e: Throwable) {
            throw InvalidRequestException("Sent \"$sendBody\"", e)
        }
    }

    suspend fun setMyCommands(client: HttpClient, token: String, commands: List<BotCommand>) {
        val sendBody =
            jsonSerialization
                .encodeToString(SetMyCommandsRequest.serializer(), SetMyCommandsRequest(commands))
        try {
            val url = BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("setMyCommands")
            val response = client.connect(HTTPMethod.POST.code, url)
                .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
                .writeTextAndGetResponse {
                    it.append(sendBody)
                }
                .readText().use {
                    it.readText()
                }
            getResult(response)
        } catch (e: Throwable) {
            throw InvalidRequestException("Sent \"$sendBody\"", e)
        }
    }

    suspend fun getMyCommands(client: HttpClient, token: String): List<BotCommand> {
        val url =
            BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("getMyCommands")
        val response = client.connect(HTTPMethod.GET.code, url)
            .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
            .getResponse()
            .readText().use {
                it.readText()
            }
        return jsonSerialization.decodeFromJsonElement(ListSerializer(BotCommand.serializer()), getResult(response)!!)
    }

    suspend fun editMessage(client: HttpClient, token: String, message: EditTextRequest): Message? {
        val sendBody = jsonSerialization.encodeToString(EditTextRequest.serializer(), message)
        try {
            val url = BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("editMessageText")
            val response = client.connect(HTTPMethod.POST.code, url)
                .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
                .writeTextAndGetResponse {
                    it.append(sendBody)
                }
                .readText().use {
                    it.readText()
                }
            val result = getResult(response) ?: return null
            return jsonSerialization.decodeFromJsonElement(Message.serializer(), result)
        } catch (e: Throwable) {
            throw InvalidRequestException("Sent \"$sendBody\"", e)
        }
    }

    suspend fun sendMessage(client: HttpClient, token: String, message: TextMessage): Message {
        val sendBody = jsonSerialization.encodeToString(TextMessage.serializer(), message)
        try {
            val url = BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("sendMessage")
            val response = client.connect(HTTPMethod.POST.code, url)
                .setHeader(Headers.CONTENT_TYPE, JSON_MIME_TYPE)
                .writeTextAndGetResponse {
                    it.append(sendBody)
                }
                .readText().use {
                    it.readText()
                }
            return jsonSerialization.decodeFromJsonElement(Message.serializer(), getResult(response)!!)
        } catch (e: Throwable) {
            throw InvalidRequestException("Sent \"$sendBody\"", e)
        }
    }

    suspend fun deleteMessage(client: HttpClient, token: String, chatId: String, messageId: Long) {
        val url =
            BASE_PATH
                .appendPath(token, direction = false, encode = true)
                .appendPath("deleteMessage")
                .appendQuery("chat_id", chatId)
                .appendQuery("message_id", messageId)
        val response = client.connect(HTTPMethod.POST.code, url)
            .getResponse()
            .readText().use {
                it.readText()
            }
        getResult(response)
    }

    suspend fun getMe(client: HttpClient, token: String): User {
        val url = BASE_PATH
            .appendPath(token, direction = false, encode = true)
            .appendPath("getMe")
        val response = client.connect(HTTPMethod.POST.code, url)
            .getResponse()
            .readText().use {
                it.readText()
            }
        return jsonSerialization.decodeFromJsonElement(User.serializer(), getResult(response)!!)
    }

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
