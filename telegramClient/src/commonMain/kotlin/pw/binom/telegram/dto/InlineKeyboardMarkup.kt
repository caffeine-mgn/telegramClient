package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents an inline keyboard that appears right next to the message it belongs to.
 */
@Serializable
class InlineKeyboardMarkup(
    /**
     * Array of button rows, each represented by an Array of [InlineKeyboardButton] objects
     */
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<InlineKeyboardButton>
) : Markup()