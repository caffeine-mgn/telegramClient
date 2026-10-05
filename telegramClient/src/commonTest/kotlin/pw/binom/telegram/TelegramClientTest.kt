package pw.binom.telegram

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import pw.binom.telegram.dto.Message
import pw.binom.telegram.dto.TextMessage
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalAtomicApi::class)
class TelegramClientTest {

    private val token = "test-token"

    private class Scenario(
        val httpClient: HttpClient,
        val lastRequest: AtomicReference<HttpRequestData?>,
    )

    private fun newScenario(handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): Scenario {
        val last = AtomicReference<HttpRequestData?>(null)
        val engine = MockEngine { request ->
            last.store(request)
            handler(request)
        }
        val http = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    classDiscriminator = "@class"
                })
            }
        }
        return Scenario(http, last)
    }

    @Test
    fun getMeReturnsUser() = runTest {
        val userJson = """
            {
              "ok": true,
              "result": {
                "id": 42,
                "is_bot": true,
                "first_name": "Test",
                "username": "test_bot"
              }
            }
        """.trimIndent()
        val s = newScenario { respond(content = userJson, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val me = tg.getMe()
        assertEquals(42L, me.id)
        assertEquals(true, me.isBot)
        assertEquals("Test", me.firstName)
        assertEquals("test_bot", me.userName)
    }

    @Test
    fun sendMessageSendsToCorrectPath() = runTest {
        val response = """
            {"ok": true, "result": {"message_id": 1, "date": 0, "chat": {"id": 1, "type": "private"}, "text": "hi"}}
        """.trimIndent()
        val s = newScenario { respond(content = response, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val msg = tg.sendMessage(TextMessage(chatId = "1", text = "hi"))
        assertEquals("hi", msg.text)
        assertEquals(1L, msg.messageId)
        val req = assertNotNull(s.lastRequest.load())
        assertEquals(HttpMethod.Post, req.method)
        assertTrue(req.url.fullPath.startsWith("/bottest-token/sendMessage"))
        assertEquals(
            ContentType.Application.Json.toString().substringBefore(";"),
            req.body.contentType?.toString()?.substringBefore(";"),
        )
    }

    @Test
    fun telegramExceptionOnApiError() = runTest {
        val errorBody = """
            {"ok": false, "error_code": 403, "description": "Forbidden"}
        """.trimIndent()
        val s = newScenario { respond(content = errorBody, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val ex = assertFailsWith<TelegramException> { tg.getMe() }
        assertEquals(403, ex.code)
        assertEquals("Forbidden", ex.description)
    }

    @Test
    fun telegramExceptionOnNon200Status() = runTest {
        val s = newScenario { respondError(status = HttpStatusCode.InternalServerError) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        assertFailsWith(IllegalArgumentException::class) { tg.getMe() }
    }

    @Test
    fun getFileUsesQueryParam() = runTest {
        val response = """
            {"ok": true, "result": {"file_id": "abc", "file_path": "documents/file.pdf", "file_unique_id": "u", "file_size": 10}}
        """.trimIndent()
        val s = newScenario { respond(content = response, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val f = tg.getFile("abc")
        assertEquals("documents/file.pdf", f.filePath)
        val req = assertNotNull(s.lastRequest.load())
        assertEquals(HttpMethod.Get, req.method)
        assertTrue(req.url.fullPath.startsWith("/bottest-token/getFile"))
        assertEquals("abc", req.url.parameters["file_id"])
    }

    @Test
    fun downloadFileReturnsRawBytes() = runTest {
        val bytes = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val s = newScenario {
            respond(
                content = bytes,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/octet-stream"),
            )
        }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val channel: ByteReadChannel = tg.downloadFile("documents/file.pdf")
        val received = channel.toByteArray()
        assertContentEquals(bytes, received)
    }

    @Test
    fun deleteMessageSendsChatIdAndMessageId() = runTest {
        val response = """{"ok": true, "result": true}"""
        val s = newScenario { respond(content = response, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        tg.deleteMessage(chatId = "999", messageId = 7L)
        val req = assertNotNull(s.lastRequest.load())
        assertEquals(HttpMethod.Post, req.method)
        assertTrue(req.url.fullPath.startsWith("/bottest-token/deleteMessage"))
        assertEquals("999", req.url.parameters["chat_id"])
        assertEquals("7", req.url.parameters["message_id"])
    }

    @Test
    fun getWebhookReturnsCurrentInfo() = runTest {
        val present = """{"ok": true, "result": {"url": "https://example.com", "has_custom_certificate": false, "pending_update_count": 0}}"""
        val s = newScenario { respond(content = present, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val wh = tg.getWebhook()
        assertEquals("https://example.com", wh.url)
        assertEquals(0, wh.pendingUpdateCount)
    }

    @Test
    fun sendVoiceSendsMultipart() = runTest {
        val messageJson = """
            {"ok": true, "result": {"message_id": 11, "date": 0, "chat": {"id": 1, "type": "private"}}}
        """.trimIndent()
        val s = newScenario { respond(content = messageJson, status = HttpStatusCode.OK) }
        val tg = TelegramClient.wrap(s.httpClient, token)
        val msg: Message = tg.sendVoice(
            chatId = "1",
            caption = "voice",
            duration = kotlin.time.Duration.parse("3s"),
            contentType = "audio/mpeg",
        ) {
            write("Hi!".encodeToByteArray())
        }
        assertEquals(11L, msg.messageId)
        val req = assertNotNull(s.lastRequest.load())
        assertEquals(HttpMethod.Post, req.method)
        assertTrue(req.url.fullPath.startsWith("/bottest-token/sendVoice"))
        val ct = assertNotNull(req.body.contentType?.toString())
        assertTrue(ct.startsWith("multipart/form-data"))
    }
}