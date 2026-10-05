package pw.binom.telegram.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * One of the inline-query result variants defined in
 * <https://core.telegram.org/bots/api#inlinequeryresult>. We expose only a generic
 * [JsonElement]-backed placeholder for now — the full sealed type set
 * (Article, Photo, Gif, Mpeg4Gif, Video, Audio, Voice, Document, Location, Venue,
 * Contact, Game, Sticker, CachedPhoto, CachedSticker, …) is large and out of scope
 * for the current iteration. Serialize the result with whatever JSON shape Telegram
 * expects and Telegram will accept it.
 */
@Serializable
data class InlineQueryResult(
    val type: String,
    val id: String,
    val raw: JsonElement = kotlinx.serialization.json.JsonNull,
)