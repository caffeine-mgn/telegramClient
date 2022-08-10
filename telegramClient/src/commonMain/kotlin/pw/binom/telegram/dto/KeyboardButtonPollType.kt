package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyboardButtonPollType(val type: PollType?)

@Serializable
enum class PollType {
    @SerialName("quiz")
    QUIZ,

    @SerialName("regular")
    REGULAR,
}
