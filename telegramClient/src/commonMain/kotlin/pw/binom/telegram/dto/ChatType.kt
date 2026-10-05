package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Type of chat, as documented at <https://core.telegram.org/bots/api#chat>
 */
@Serializable
enum class ChatType {
    @SerialName("private")
    Private,

    @SerialName("group")
    Group,

    @SerialName("supergroup")
    Supergroup,

    @SerialName("channel")
    Channel,
}