package pw.binom.telegram.dto

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents a Telegram user or bot.
 */
@Serializable
class User(
        /**
         * Unique identifier for this user or bot
         */
        val id: Long,

        /**
         * If this user is a bot
         */
        @SerialName("is_bot")
        val isBot: Boolean,

        /**
         * User's or bot's first name
         */
        @SerialName("first_name")
        val firstName: String,

        /**
         * User's or bot's last name
         */
        @SerialName("last_name")
        val lastName: String? = null,

        /**
         * User's or bot's username
         */
        @SerialName("username")
        val userName: String?=null,

        /**
         * IETF language tag of the user's language
         */
        @SerialName("language_code")
        val languageCode: String?=null,

        /**
         * True, if the bot can be invited to groups. Returned only in getMe.
         */
        @SerialName("can_join_groups")
        val canJoinGroups: Boolean?=null,

        /**
         * True, if privacy mode is disabled for the bot. Returned only in getMe.
         */
        @SerialName("can_read_all_group_messages")
        val canReadAllGroupMessages: Boolean?=null,

        /**
         * True, if the bot supports inline queries. Returned only in getMe.
         */
        @SerialName("supports_inline_queries")
        val supportsInlineQueries: Boolean?=null
)