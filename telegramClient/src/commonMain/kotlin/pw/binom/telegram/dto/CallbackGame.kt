package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CallbackGame(
    /**
     * User identifier
     */
    @SerialName("user_id")
    val userId: Int,
    /**
     * New score, must be non-negative
     */
    val score: Int,

    /**
     * Pass True, if the high score is allowed to decrease. This can be useful when fixing mistakes or banning cheaters
     */
    val force: Boolean?,

    /**
     * Pass True, if the game message should not be automatically edited to include the current scoreboard
     */
    @SerialName("disable_edit_message")
    val disableEditMessage: Boolean?,

    /**
     * Required if inline_message_id is not specified. Unique identifier for the target chat
     */
    @SerialName("chat_id")
    val chatId: Int?,
    /**
     * Required if inline_message_id is not specified. Identifier of the sent message
     */
    @SerialName("message_id")
    val messageId: Int?,

    /**
     * Required if chat_id and message_id are not specified. Identifier of the inline message
     */
    @SerialName("inline_message_id")
    val inlineMessageId: String?
)