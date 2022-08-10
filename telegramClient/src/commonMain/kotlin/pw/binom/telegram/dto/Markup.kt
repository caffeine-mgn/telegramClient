package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Markup

@Serializable
class ForceReply : Markup()

/**
 * This object represents an inline keyboard that appears right next to the message it belongs to.
 */
@Serializable
data class InlineKeyboardMarkup(
    /**
     * Array of button rows, each represented by an Array of [InlineKeyboardButton] objects
     */
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboardButton>>
) : Markup()

@Serializable
data class ReplyKeyboardRemove(
    @SerialName("remove_keyboard")
    val removeKeyboard: Boolean,

    @SerialName("selective")
    val selective: Boolean? = null
) : Markup()

@Serializable
data class ReplyKeyboardMarkup(
    @SerialName("keyboard")
    val keyboard: List<List<KeyboardButton>>,

    @SerialName("resize_keyboard")
    val resizeKeyboard: Boolean? = null,

    @SerialName("one_time_keyboard")
    val oneTimeKeyboard: Boolean? = null,

    @SerialName("selective")
    val selective: Boolean? = null,
) : Markup()
