package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyboardButton(
    val text: String,

    @SerialName("request_contact")
    val requestContact: Boolean? = null,

    @SerialName("request_location")
    val requestLocation: Boolean? = null,

    @SerialName("request_poll")
    val requestPoll: KeyboardButtonPollType? = null
)
