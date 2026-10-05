package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One of the options a poll can vote for. Used by `sendPoll`.
 */
@Serializable
data class InputPollOption(
    @SerialName("text")
    val text: String,

    @SerialName("text_parse_mode")
    val textParseMode: ParseMode? = null,

    @SerialName("text_entities")
    val textEntities: List<MessageEntity>? = null,
)

/**
 * Body of [TelegramApi.answerInlineQuery]. `cache_time` and `is_personal` default to
 * Telegram's defaults if not set.
 */
@Serializable
data class AnswerInlineQueryRequest(
    @SerialName("inline_query_id")
    val inlineQueryId: String,

    val results: List<InlineQueryResult>,

    @SerialName("cache_time")
    val cacheTime: Int? = null,

    @SerialName("is_personal")
    val isPersonal: Boolean? = null,

    @SerialName("next_offset")
    val nextOffset: String? = null,

    @SerialName("button")
    val button: InlineQueryResultsButton? = null,
)

@Serializable
data class InlineQueryResultsButton(
    val text: String,

    @SerialName("web_app")
    val webApp: WebAppInfo? = null,

    @SerialName("start_parameter")
    val startParameter: String? = null,
)

@Serializable
data class WebAppInfo(val url: String)