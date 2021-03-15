package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents one button of an inline keyboard. You must use exactly one of the optional fields.
 */
@Serializable
data class InlineKeyboardButton(
    /**
     * Label text on the button
     */
    val text: String,
    /**
     * HTTP or tg:// url to be opened when button is pressed
     */
    val url: String? = null,

    /**
     * An HTTP URL used to automatically authorize the user. Can be used as a replacement for the Telegram Login Widget.
     */
    @SerialName("login_url")
    val loginUrl: LoginUrl? = null,

    /**
     * Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes
     */
    @SerialName("callback_data")
    val callbackData: String? = null,

    /**
     * If set, pressing the button will prompt the user to select one of their chats, open that chat and insert the bot's username and the specified inline query in the input field. Can be empty, in which case just the bot's username will be inserted.
     * Note: This offers an easy way for users to start using your bot in inline mode when they are currently in a private chat with it. Especially useful when combined with switch_pm… actions – in this case the user will be automatically returned to the chat they switched from, skipping the chat selection screen.
     */
    @SerialName("switch_inline_query")
    val switchInlineQuery: String? = null,

    /**
     * If set, pressing the button will insert the bot's username and the specified inline query in the current chat's input field. Can be empty, in which case only the bot's username will be inserted.
     * This offers a quick way for the user to open your bot in inline mode in the same chat – good for selecting something from multiple options.
     */
    @SerialName("switch_inline_query_current_chat")
    val switchInlineQueryCurrentChat: String? = null,


    /**
     * Description of the game that will be launched when the user presses the button.
     * NOTE: This type of button must always be the first button in the first row.
     */
    @SerialName("callback_game")
    val callbackGame: CallbackGame? = null,


    /**
     * Specify True, to send a Pay button.
     * NOTE: This type of button must always be the first button in the first row.
     */
    val pay: Boolean? = null,
)