package pw.binom.telegram

import kotlinx.coroutines.test.runTest
import pw.binom.System
import pw.binom.asyncInput
import pw.binom.copyTo
import pw.binom.http.client.HttpClientRunnable
import pw.binom.http.client.factory.Https11ConnectionFactory
import pw.binom.http.client.factory.NativeNetChannelFactory
import pw.binom.io.file.File
import pw.binom.io.file.openRead
import pw.binom.io.httpClient.HttpClient
import pw.binom.io.httpClient.create
import pw.binom.io.readBytes
import pw.binom.io.use
import pw.binom.io.useAsync
import pw.binom.network.MultiFixedSizeThreadNetworkDispatcher
import pw.binom.telegram.dto.TextMessage
import pw.binom.thread.Thread
import pw.binom.uuid.nextUuid
import kotlin.random.Random
import kotlin.test.Test

class TelegramClientTest {
    @Test
    fun test() = runTest {
        MultiFixedSizeThreadNetworkDispatcher(4).use { networkManager ->
            HttpClientRunnable(
                source = NativeNetChannelFactory(
                    manager = networkManager
                ),
                factory = Https11ConnectionFactory(),
            ).useAsync { client ->
                val file = File("/tmp/ff/audio.mp3")
//                val file = File("/home/subochev/nvidia.hook")
//                TelegramApi.sendMessage(
//                    client = client,
//                    token = "7865061851:AAHEDjj90tYeMRVt4poGvxNzpEu7rbTTmrc",
////                    chatId = "119706406",
//                    message = TextMessage(
//                        chatId = "119706406",
//                        text = "test",
//                    )
//                )
                val fileId = "AwACAgIAAxkDAAICnGgmHZe29o6Btuk7WPqt_hSsQxyuAALZcgACEN8wSWWI4_sHCrl2NgQ"
                val fileT = TelegramApi.getFile(
                    client = client,
                    token = "7865061851:AAHEDjj90tYeMRVt4poGvxNzpEu7rbTTmrc",
                    fileId = fileId
                ).filePath!!
                TelegramApi.downloadFile(
                    client = client,
                    token = "7865061851:AAHEDjj90tYeMRVt4poGvxNzpEu7rbTTmrc",
                    filePath = fileT,
                ).useAsync { response ->
                    val bytes = response.readBytes()
                    println("Bytes: ${bytes.size}")
                }
//                TelegramApi.sendVoice(
//                    client = client,
//                    token = "7865061851:AAHEDjj90tYeMRVt4poGvxNzpEu7rbTTmrc",
//                    chatId = "119706406",
////                    contentType = "audio/mpeg",
//                ) { multipart ->
//                    file.openRead().asyncInput().useAsync { f -> f.copyTo(multipart) }
//                }
            }
        }
    }
}
