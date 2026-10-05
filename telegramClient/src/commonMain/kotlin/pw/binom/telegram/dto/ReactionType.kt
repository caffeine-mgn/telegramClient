package pw.binom.telegram.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Marker type for Bot API objects we expose but don't deserialize in detail yet.
 * Use [jsonElement] for full control over the raw JSON value.
 */
@Serializable
data class ReactionType(
    val type: String? = null,
    val emoji: String? = null,
    val custom_emoji_id: String? = null,
    val jsonElement: JsonElement = kotlinx.serialization.json.JsonNull,
)