package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an invitation link for a chat.
 *
 * See <https://core.telegram.org/bots/api#chatinvitelink>.
 */
@Serializable
data class ChatInviteLink(
    @SerialName("invite_link")
    val inviteLink: String,

    val creator: User,

    @SerialName("creates_join_request")
    val createsJoinRequest: Boolean,

    @SerialName("is_primary")
    val isPrimary: Boolean,

    @SerialName("is_revoked")
    val isRevoked: Boolean,

    @SerialName("name")
    val name: String? = null,

    @SerialName("expire_date")
    val expireDate: Long? = null,

    @SerialName("member_limit")
    val memberLimit: Int? = null,

    @SerialName("pending_join_request_count")
    val pendingJoinRequestCount: Int? = null,

    @SerialName("subscription_period")
    val subscriptionPeriod: Int? = null,

    @SerialName("subscription_price")
    val subscriptionPrice: Int? = null,
)

/**
 * Reply of [TelegramApi.copyMessage] / [TelegramApi.copyMessages]. Telegram returns the new
 * message identifier without the rest of the [Message] envelope.
 */
@Serializable
data class MessageId(
    @SerialName("message_id")
    val messageId: Long,
)