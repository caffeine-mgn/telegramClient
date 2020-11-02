package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WebhookInfo(
        /**
         * Webhook URL, may be empty if webhook is not set up
         */
        @SerialName("url")
        val url: String,

        /**
         * True, if a custom certificate was provided for webhook certificate checks
         */
        @SerialName("has_custom_certificate")
        val has_custom_certificate: Boolean,

        /**
         * Number of updates awaiting delivery
         */
        @SerialName("pending_update_count")
        val pending_update_count: Int,

        /**
         * Unix time for the most recent error that happened when trying to deliver an update via webhook
         */
        @SerialName("last_error_date")
        val last_error_date: Long? = null,

        /**
         * Error message in human-readable format for the most recent error that happened when trying to deliver an update via webhook
         */
        @SerialName("last_error_message")
        val last_error_message: String? = null,

        /**
         * Maximum allowed number of simultaneous HTTPS connections to the webhook for update delivery
         */
        @SerialName("max_connections")
        val max_connections: Int? = null,

        /**
         * A list of update types the bot is subscribed to. Defaults to all update
         */
        @SerialName("allowed_updates")
        val allowed_updates: Array<String>? = null,
)