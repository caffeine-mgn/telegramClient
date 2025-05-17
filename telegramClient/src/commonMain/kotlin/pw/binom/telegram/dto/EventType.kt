package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EventType {
    /**
     * New incoming message of any kind — text, photo, sticker, etc.
     */
    @SerialName("message")
    MESSAGE,

    /**
     * New version of a message that is known to the bot and was edited
     */
    @SerialName("edited_message")
    EDITED_MESSAGE,

    /**
     * New incoming channel post of any kind — text, photo, sticker, etc.
     */
    @SerialName("channel_post")
    CHANNEL_POST,

    /**
     * New version of a channel post that is known to the bot and was edited
     */
    @SerialName("edited_channel_post")
    EDITED_CHANNEL_POST,

    /**
     * New incoming inline query
     */
    @SerialName("inline_query")
    INLINE_QUERY,

    /**
     * The result of an inline query that was chosen by a user and sent to their chat partner. Please see our documentation on the feedback collecting for details on how to enable these updates for your bot.
     */
    @SerialName("chosen_inline_result")
    CHOSEN_INLINE_RESULT,

    /**
     * New incoming callback query
     */
    @SerialName("callback_query")
    CALLBACK_QUERY,

    /**
     * New incoming shipping query. Only for invoices with flexible price
     */
    @SerialName("shipping_query")
    SHIPPING_QUERY,

    /**
     * New incoming pre-checkout query. Contains full information about checkout
     */
    @SerialName("pre_checkout_query")
    PRE_CHECKOUT_QUERY,

    /**
     * New poll state. Bots receive only updates about stopped polls and polls, which are sent by the bot
     */
    @SerialName("poll")
    POLL,

    /**
     * A user changed their answer in a non-anonymous poll. Bots receive new votes only in polls that were sent by the bot itself.
     */
    @SerialName("poll_answer")
    POLL_ANSWER,

    /**
     * The bot's chat member status was updated in a chat. For private chats, this update is received only when the bot is blocked or unblocked by the user.
     */
    @SerialName("my_chat_member")
    MY_CHAT_MEMBER,

    /**
     * A chat member's status was updated in a chat. The bot must be an administrator in the chat and must explicitly specify “chat_member” in the list of allowed_updates to receive these updates.
     */
    @SerialName("chat_member")
    CHAT_MEMBER;
}
