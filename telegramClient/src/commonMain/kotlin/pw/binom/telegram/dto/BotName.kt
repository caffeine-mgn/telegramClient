package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotName(
    val name: String,
)

@Serializable
data class BotShortDescription(
    @SerialName("short_description")
    val shortDescription: String,
)

@Serializable
data class BotDescription(
    val description: String,
)