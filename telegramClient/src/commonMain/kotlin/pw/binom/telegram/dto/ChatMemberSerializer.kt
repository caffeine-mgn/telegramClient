package pw.binom.telegram.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

internal object ChatMemberSerializer : KSerializer<ChatMember> {

    private val json = Json { ignoreUnknownKeys = true }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ChatMember")

    override fun deserialize(decoder: Decoder): ChatMember {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        val obj = element as? JsonObject
            ?: throw SerializationException("ChatMember expected to be a JSON object: $element")
        val status = obj["status"]?.jsonPrimitive?.content
            ?: throw SerializationException("ChatMember missing status field: $element")
        return when (status) {
            "creator" -> json.decodeFromJsonElement(ChatMemberOwner.serializer(), element)
            "administrator" -> json.decodeFromJsonElement(ChatMemberAdministrator.serializer(), element)
            "member" -> json.decodeFromJsonElement(ChatMemberMember.serializer(), element)
            "restricted" -> json.decodeFromJsonElement(ChatMemberRestricted.serializer(), element)
            "left" -> json.decodeFromJsonElement(ChatMemberLeft.serializer(), element)
            "kicked" -> json.decodeFromJsonElement(ChatMemberBanned.serializer(), element)
            else -> throw SerializationException("Unknown ChatMember status: $status")
        }
    }

    override fun serialize(encoder: Encoder, value: ChatMember) {
        val element: JsonElement = when (value) {
            is ChatMemberOwner -> json.encodeToJsonElement(ChatMemberOwner.serializer(), value)
            is ChatMemberAdministrator -> json.encodeToJsonElement(ChatMemberAdministrator.serializer(), value)
            is ChatMemberMember -> json.encodeToJsonElement(ChatMemberMember.serializer(), value)
            is ChatMemberRestricted -> json.encodeToJsonElement(ChatMemberRestricted.serializer(), value)
            is ChatMemberLeft -> json.encodeToJsonElement(ChatMemberLeft.serializer(), value)
            is ChatMemberBanned -> json.encodeToJsonElement(ChatMemberBanned.serializer(), value)
        }
        (encoder as kotlinx.serialization.json.JsonEncoder).encodeJsonElement(element)
    }
}