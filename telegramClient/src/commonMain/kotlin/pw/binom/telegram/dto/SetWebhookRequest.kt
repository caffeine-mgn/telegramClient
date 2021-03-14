package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SetWebhookRequest(
    @SerialName("url")
    val url: String,

    @SerialName("certificate")
    val certificate: String? = null,

    @SerialName("max_connections")
    val maxConnections: Int? = null,

    @SerialName("allowed_updates")
    val allowedUpdates: List<EventType>? = null
)