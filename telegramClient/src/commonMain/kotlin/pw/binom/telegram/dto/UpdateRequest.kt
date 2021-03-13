package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequest(
    val offset: Long?,
    val limit: Long?,
    val timeout: Long,

    @SerialName("allowed_updates")
    val allowedUpdates: List<String>?,
)