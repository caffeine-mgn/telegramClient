package pw.binom.telegram.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * A content to send on inline mode, or a media to attach to a message via [editMessageMedia].
 * Telegram wraps every media in an `InputMedia` JSON object whose `type` is the kind
 * (`photo`, `video`, `animation`, `audio`, `document`).
 *
 * The model is intentionally narrow: [Photo], [Video], [Animation], [Audio], [Document].
 * Build it with the data class you need and pass it straight to [editMessageMedia] or to
 * `sendMediaGroup`. The `media` field is an [InputFile], so any of the three transport
 * modes (file_id / URL / streamed multipart) is supported.
 */
@Serializable(with = InputMediaSerializer::class)
sealed class InputMedia {
    abstract val type: String
    abstract val media: InputFile
    abstract val caption: String?
    abstract val parseMode: ParseMode?
    abstract val captionEntities: List<MessageEntity>?

    @Serializable
    data class Photo(
        override val media: InputFile,
        override val caption: String? = null,
        override val parseMode: ParseMode? = null,
        override val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        @SerialName("has_spoiler") val hasSpoiler: Boolean? = null,
    ) : InputMedia() {
        override val type: String get() = "photo"
    }

    @Serializable
    data class Video(
        override val media: InputFile,
        override val caption: String? = null,
        override val parseMode: ParseMode? = null,
        override val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        val width: Int? = null,
        val height: Int? = null,
        val duration: Int? = null,
        @SerialName("supports_streaming") val supportsStreaming: Boolean? = null,
        @SerialName("has_spoiler") val hasSpoiler: Boolean? = null,
    ) : InputMedia() {
        override val type: String get() = "video"
    }

    @Serializable
    data class Animation(
        override val media: InputFile,
        override val caption: String? = null,
        override val parseMode: ParseMode? = null,
        override val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        val width: Int? = null,
        val height: Int? = null,
        val duration: Int? = null,
        @SerialName("has_spoiler") val hasSpoiler: Boolean? = null,
    ) : InputMedia() {
        override val type: String get() = "animation"
    }

    @Serializable
    data class Audio(
        override val media: InputFile,
        override val caption: String? = null,
        override val parseMode: ParseMode? = null,
        override val captionEntities: List<MessageEntity>? = null,
        val duration: Int? = null,
        val performer: String? = null,
        val title: String? = null,
    ) : InputMedia() {
        override val type: String get() = "audio"
    }

    @Serializable
    data class Document(
        override val media: InputFile,
        override val caption: String? = null,
        override val parseMode: ParseMode? = null,
        override val captionEntities: List<MessageEntity>? = null,
        @SerialName("disable_content_type_detection") val disableContentTypeDetection: Boolean? = null,
    ) : InputMedia() {
        override val type: String get() = "document"
    }
}

internal object InputMediaSerializer : KSerializer<InputMedia> {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InputMedia")

    override fun deserialize(decoder: Decoder): InputMedia {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        val obj = element as? JsonObject
            ?: throw SerializationException("InputMedia expected to be a JSON object: $element")
        val type = obj["type"]?.jsonPrimitive?.content
            ?: throw SerializationException("InputMedia missing type: $element")
        return when (type) {
            "photo" -> json.decodeFromJsonElement(InputMedia.Photo.serializer(), element)
            "video" -> json.decodeFromJsonElement(InputMedia.Video.serializer(), element)
            "animation" -> json.decodeFromJsonElement(InputMedia.Animation.serializer(), element)
            "audio" -> json.decodeFromJsonElement(InputMedia.Audio.serializer(), element)
            "document" -> json.decodeFromJsonElement(InputMedia.Document.serializer(), element)
            else -> throw SerializationException("Unsupported InputMedia type: $type")
        }
    }

    override fun serialize(encoder: Encoder, value: InputMedia) {
        val element: JsonElement = when (value) {
            is InputMedia.Photo -> json.encodeToJsonElement(InputMedia.Photo.serializer(), value)
            is InputMedia.Video -> json.encodeToJsonElement(InputMedia.Video.serializer(), value)
            is InputMedia.Animation -> json.encodeToJsonElement(InputMedia.Animation.serializer(), value)
            is InputMedia.Audio -> json.encodeToJsonElement(InputMedia.Audio.serializer(), value)
            is InputMedia.Document -> json.encodeToJsonElement(InputMedia.Document.serializer(), value)
        }
        (encoder as JsonEncoder).encodeJsonElement(element)
    }
}