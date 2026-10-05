package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookInfo(
    /**
     * Webhook URL, may be empty if webhook is not set up
     */
    @SerialName("url")
    val url: String? = null,

    /**
     * True, if a custom certificate was provided for webhook certificate checks
     */
    @SerialName("has_custom_certificate")
    val hasCustomCertificate: Boolean,

    /**
     * Number of updates awaiting delivery
     */
    @SerialName("pending_update_count")
    val pendingUpdateCount: Int,

    /**
     * Optional. Currently used webhook IP address
     */
    @SerialName("ip_address")
    val ipAddress: String? = null,

    /**
     * Optional. Unix time for the most recent error that happened when trying to deliver an update via webhook
     */
    @SerialName("last_error_date")
    val lastErrorDate: Long? = null,

    /**
     * Optional. Error message in human-readable format for the most recent error that happened when trying to deliver an update via webhook
     */
    @SerialName("last_error_message")
    val lastErrorMessage: String? = null,

    /**
     * Optional. Unix time of the most recent error that happened when trying to synchronize available updates with Telegram datacenters
     */
    @SerialName("last_synchronization_error_date")
    val lastSynchronizationErrorDate: Long? = null,

    /**
     * Optional. The maximum allowed number of simultaneous HTTPS connections to the webhook for update delivery
     */
    @SerialName("max_connections")
    val maxConnections: Int? = null,

    /**
     * Optional. A list of update types the bot is subscribed to. Defaults to all update types except chat_member, message_reaction, and message_reaction_count.
     */
    @SerialName("allowed_updates")
    val allowedUpdates: List<EventType>? = null,
)