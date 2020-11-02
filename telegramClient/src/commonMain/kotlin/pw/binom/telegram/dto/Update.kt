package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Update(
        @SerialName("update_id")
        val updateId: Long,

        val message: Message? = null,

        @SerialName("edited_message")
        val editedMessage: Message? = null,

        @SerialName("channel_post")
        val channelPost: Message? = null,

        @SerialName("edited_channel_post")
        val editedChannelPost: Message? = null,

        @SerialName("inline_query")
        val inlineQuery: InlineQuery? = null,

        @SerialName("chosen_inline_result")
        val chosenInlineResult: ChosenInlineResult? = null,

        @SerialName("callback_query")
        val callbackQuery: CallbackQuery? = null,

        @SerialName("shipping_query")
        val shippingQuery: ShippingQuery? = null,

        @SerialName("pre_checkout_query")
        val preCheckoutQuery: PreCheckoutQuery? = null,

        val poll: Poll? = null,

        @SerialName("poll_answer")
        val pollAnswer: PollAnswer? = null
)