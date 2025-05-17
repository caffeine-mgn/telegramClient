package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequest(
    var offset: Long?,
    /**
     * Timeout in seconds
     */
    var limit: Long?,
    var timeout: Long,

    @SerialName("allowed_updates")
    var allowedUpdates: List<EventType>?,
)
