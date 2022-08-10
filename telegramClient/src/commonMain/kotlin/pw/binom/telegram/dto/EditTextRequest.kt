package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditTextRequest(
    /**
     * Required if inline_message_id is not specified. Unique identifier for the target chat or username of the target channel (in the format @channelusername)
     */
    @SerialName("chat_id")
    val chatId: String? = null,

    /**
     * Required if inline_message_id is not specified. Identifier of the message to edit
     */
    @SerialName("message_id")
    val messageId: Long? = null,
    /**
     * Required if chat_id and message_id are not specified. Identifier of the inline message
     */
    @SerialName("inline_message_id")
    val inlineMessageId: String? = null,

    /**
     * New text of the message, 1-4096 characters after entities parsing
     */
    @SerialName("text")
    val text: String,

    /**
     * Mode for parsing entities in the message text. See formatting options for more details.
     */
    @SerialName("parse_mode")
    val parseMode: ParseMode? = null,

    /**
     * List of special entities that appear in message text, which can be specified instead of parse_mode
     */
    @SerialName("entities")
    val entities: List<MessageEntity>? = null,

    /**
     * Disables link previews for links in this message
     */
    @SerialName("disable_web_page_preview")
    val disableWebPagePreview: Boolean? = null,

    /**
     * A JSON-serialized object for an inline keyboard.
     */
    @SerialName("reply_markup")
    val replyMarkup: InlineKeyboardMarkup? = null,
)
