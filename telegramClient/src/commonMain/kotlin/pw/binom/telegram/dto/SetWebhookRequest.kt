package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SetWebhookRequest(
        @SerialName("url")
        val url: String,

        @SerialName("certificate")
        val certificate: String?,

        @SerialName("max_connections")
        val maxConnections: Int?,

        @SerialName("allowed_updates")
        val allowedUpdates: List<String>?
)