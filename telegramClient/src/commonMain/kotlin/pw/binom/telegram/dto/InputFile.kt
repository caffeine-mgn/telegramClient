package pw.binom.telegram.dto

import kotlinx.io.Sink
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents the contents of a file to be uploaded. There are three ways to ship file data
 * to the Bot API:
 *
 * - [FileId]: reuse a file already on Telegram's servers (cheapest — no upload).
 * - [Url]: pass a public HTTPS URL (Telegram fetches it).
 * - [Multipart]: stream a payload from a [Sink] callback — the file content never has to live
 *   in memory as a single [ByteArray]; the bytes are produced lazily by the caller inside
 *   `data` and forwarded straight to the HTTP request body.
 *
 * `Multipart` can only be used with multipart endpoints. Use it for things like [sendPhoto],
 * [sendDocument], [editMessageMedia], [setWebhook] (certificate), etc. The JSON serializer
 * throws if it ever meets a [Multipart] — Use [Webpage] / [FileId] when the endpoint only
 * accepts a string.
 */
@Serializable(with = InputFileSerializer::class)
sealed class InputFile {

    /**
     * Reuse a file already on Telegram's servers. The string is the value of a [File.fileId].
     */
    data class FileId(val fileId: String) : InputFile()

    /**
     * Tell Telegram to fetch the file from a public HTTPS URL.
     */
    data class Url(val url: String) : InputFile()

    /**
     * Stream a payload to Telegram. The lambda [data] is called once with a [Sink] receiver
     * — write whatever bytes you have into it. The lambda runs as part of the HTTP request
     * body construction, so chunked transfer encoding is fine for arbitrarily large files.
     *
     * @param fileName filename to advertise via Content-Disposition.
     * @param contentType MIME type, e.g. `image/png`. Defaults to `application/octet-stream`.
     * @param size optional expected length; if `null`, Telegram receives chunked data.
     * @param data the producer — receives a [Sink] and writes bytes into it.
     */
    data class Multipart(
        val fileName: String,
        val contentType: String = "application/octet-stream",
        val size: Long? = null,
        val data: Sink.() -> Unit,
    ) : InputFile()
}

internal object InputFileSerializer : KSerializer<InputFile> {
    @Serializable
    private class Surrogate(val value: String)

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        serialName = "InputFile",
        kind = SerialKind.CONTEXTUAL,
    )

    override fun serialize(encoder: Encoder, value: InputFile) {
        when (value) {
            is InputFile.FileId -> encoder.encodeString(value.fileId)
            is InputFile.Url -> encoder.encodeString(value.url)
            is InputFile.Multipart ->
                error("InputFile.Multipart can only be used in multipart endpoints (sendPhoto, setWebhook, ...)")
        }
    }

    override fun deserialize(decoder: Decoder): InputFile {
        val str = decoder.decodeString()
        return if (str.startsWith("http://") || str.startsWith("https://")) InputFile.Url(str)
        else InputFile.FileId(str)
    }
}