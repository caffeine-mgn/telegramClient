package pw.binom.telegram.dto

import kotlinx.serialization.Serializable

/**
 * This object represents an incoming callback query from a callback button in an inline keyboard. If the button that originated the query was attached to a message sent by the bot, the field message will be present. If the button was attached to a message sent via the bot (in inline mode), the field inline_message_id will be present. Exactly one of the fields data or game_short_name will be present.
 */
@Serializable
class CallbackQuery(
        /**
         * Unique identifier for this query
         */
        val id: String,

        /**
         * Sender
         */
        val from: User,

        /**
         * Message with the callback button that originated the query. Note that message content and message date will not be available if the message is too old
         */
        val message: Message? = null,

        /**
         * Identifier of the message sent via the bot in inline mode, that originated the query.
         */
        val inline_message_id: String? = null,

        /**
         * Global identifier, uniquely corresponding to the chat to which the message with the callback button was sent. Useful for high scores in games.
         */
        val chat_instance: String,

        /**
         * Data associated with the callback button. Be aware that a bad client can send arbitrary data in this field.
         */
        val data: String? = null,

        /**
         * Short name of a Game to be returned, serves as the unique identifier for the game
         */
        val game_short_name: String? = null
)