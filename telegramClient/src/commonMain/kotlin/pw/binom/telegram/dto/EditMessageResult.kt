package pw.binom.telegram.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.encodeToJsonElement

/**
 * The result of [editMessageText]. Telegram returns the edited [Message] when the
 * message is in a regular chat, and returns `True` when the message is an inline
 * message (see <https://core.telegram.org/bots/api#editmessagetext>).
 */
@Serializable(with = EditMessageResultSerializer::class)
sealed class EditMessageResult {
    data class Edited(val message: Message) : EditMessageResult()
    data object Inline : EditMessageResult()
}

internal object EditMessageResultSerializer : KSerializer<EditMessageResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EditMessageResult")

    override fun deserialize(decoder: Decoder): EditMessageResult {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        return when (element) {
            is JsonPrimitive -> if (element.boolean) EditMessageResult.Inline
            else error("editMessage returned non-true JsonPrimitive: $element")
            is JsonObject -> EditMessageResult.Edited(
                json.decodeFromJsonElement(Message.serializer(), element),
            )
            else -> error("Unexpected editMessage result: $element")
        }
    }

    override fun serialize(encoder: Encoder, value: EditMessageResult) {
        val jsonEncoder = encoder as JsonEncoder
        val element: JsonElement = when (value) {
            is EditMessageResult.Edited -> json.encodeToJsonElement(Message.serializer(), value.message)
            EditMessageResult.Inline -> JsonPrimitive(true)
        }
        jsonEncoder.encodeJsonElement(element)
    }
}

private val json = Json { ignoreUnknownKeys = true }