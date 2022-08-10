package pw.binom.telegram.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetMyCommandsRequest(val commands: List<BotCommand>)
