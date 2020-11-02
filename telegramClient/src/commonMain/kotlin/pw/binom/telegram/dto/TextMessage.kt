package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TextMessage(
        /**
         * Unique identifier for the target chat or username of the target channel (in the format @channelusername)
         */
        @SerialName("chat_id")
        val chat_id: String,

        /**
         * Text of the message to be sent, 1-4096 characters after entities parsing
         */
        @SerialName("text")
        val text: String,

        /**
         * Mode for parsing entities in the message text. See [ParseMode] for more details.
         */
        @SerialName("parse_mode")
        val parseMode: ParseMode? = null,

        /**
         * Disables link previews for links in this message
         */
        @SerialName("disable_web_page_preview")
        val disableWebPagePreview: Boolean? = null,

        /**
         * Sends the message silently. Users will receive a notification with no sound.
         */
        @SerialName("disable_notification")
        val disableNotification: Boolean? = null,

        /**
         * If the message is a reply, ID of the original message
         */
        @SerialName("reply_to_message_id")
        val replyToMessageId: Long? = null,

        /**
         * Additional interface options. A JSON-serialized object for an inline keyboard, custom reply keyboard, instructions to remove reply keyboard or to force a reply from the user.
         */
        @SerialName("reply_markup")
        val replyMarkup: Markup? = null
)