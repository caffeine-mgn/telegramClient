package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerCallbackQueryRequest(
    /**
     * Unique identifier for the query to be answered
     */
    @SerialName("callback_query_id")
    val callbackQueryId: String,

    /**
     * Text of the notification. If not specified, nothing will be shown to the user, 0-200 characters
     */
    @SerialName("text")
    val text: String? = null,

    /**
     * If true, an alert will be shown by the client instead of a notification at the top of the chat screen.
     * Defaults to false.
     */
    @SerialName("show_alert")
    val showAlert: Boolean? = null,

    /**
     * URL that will be opened by the user's client. If you have created a Game and accepted the conditions
     * via @Botfather, specify the URL that opens your game — note that this will only work if the query comes
     * from a callback_game button.
     * Otherwise, you may use links like t.me/your_bot?start=XXXX that open your bot with a parameter.
     */
    @SerialName("url")
    val url: String? = null,

    /**
     * The maximum amount of time in seconds that the result of the callback query may be cached client-side.
     * Telegram apps will support caching starting in version 3.14. Defaults to 0.
     */
    @SerialName("cache_time")
    val cacheTime: Long? = null
)