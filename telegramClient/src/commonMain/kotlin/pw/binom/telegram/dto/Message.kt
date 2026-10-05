package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents a message.
 */
@Serializable
data class Message(
    @SerialName("message_id")
    val messageId: Long? = null,

    /**
     * Optional. Unique identifier of a message thread to which the message belongs; for supergroups only
     */
    @SerialName("message_thread_id")
    val messageThreadId: Long? = null,

    /**
     * Optional. Sender of the message; empty for messages sent on behalf of a chat.
     * For backward compatibility, may be `null` if the message was sent on behalf of a chat.
     */
    val from: User? = null,

    /**
     * Optional. Sender of the message when sent on behalf of a chat. For messages forwarded by the bot, sender of the original message.
     */
    @SerialName("sender_chat")
    val senderChat: Chat? = null,

    /**
     * Optional. If the sender of the message boosted the chat, the number of boosts added by the user
     */
    @SerialName("sender_boost_count")
    val senderBoostCount: Int? = null,

    /**
     * Optional. The bot that actually sent the message on behalf of the user (forwarded by the bot)
     */
    @SerialName("via_bot")
    val viaBot: User? = null,

    val date: Long? = null,
    val chat: Chat? = null,

    @SerialName("forward_from")
    val forwardFrom: User? = null,

    @SerialName("forward_from_chat")
    val forwardFromChat: Chat? = null,

    @SerialName("forward_from_message_id")
    val forwardFromMessageId: Long? = null,

    @SerialName("forward_signature")
    val forwardSignature: String? = null,

    @SerialName("forward_sender_name")
    val forwardSenderName: String? = null,

    @SerialName("forward_date")
    val forwardDate: Long? = null,

    /**
     * Optional. True, if the message is a channel post that was automatically forwarded to the connected discussion group
     */
    @SerialName("is_automatic_forward")
    val isAutomaticForward: Boolean? = null,

    @SerialName("reply_to_message")
    val replyToMessage: Message? = null,

    /**
     * Optional. True, if the message is a topic message
     */
    @SerialName("is_topic_message")
    val isTopicMessage: Boolean? = null,

    @SerialName("edit_date")
    val editDate: Long? = null,

    @SerialName("has_protected_content")
    val hasProtectedContent: Boolean? = null,

    @SerialName("media_group_id")
    val mediaGroupId: String? = null,

    @SerialName("author_signature")
    val authorSignature: String? = null,

    val text: String? = null,
    val entities: List<MessageEntity>? = null,
    val animation: Animation? = null,
    val audio: Audio? = null,
    val document: Document? = null,

    @SerialName("photo")
    val photo: List<PhotoSize>? = null,
    val sticker: Sticker? = null,
    val video: Video? = null,

    @SerialName("video_note")
    val videoNote: VideoNote? = null,
    val voice: Voice? = null,
    val caption: String? = null,

    @SerialName("caption_entities")
    val captionEntities: List<MessageEntity>? = null,
    val contact: Contact? = null,
    val dice: Dice? = null,
    val game: Game? = null,
    val poll: Poll? = null,
    val venue: Venue? = null,
    val location: Location? = null,

    @SerialName("new_chat_members")
    val newChatMembers: List<User>? = null,

    @SerialName("left_chat_member")
    val leftChatMember: User? = null,

    @SerialName("new_chat_title")
    val newChatTitle: String? = null,

    @SerialName("new_chat_photo")
    val newChatPhoto: List<PhotoSize>? = null,

    @SerialName("delete_chat_photo")
    val deleteChatPhoto: Boolean? = null,

    @SerialName("group_chat_created")
    val groupChatCreated: Boolean? = null,

    @SerialName("supergroup_chat_created")
    val supergroupChatCreated: Boolean? = null,

    @SerialName("channel_chat_created")
    val channelChatCreated: Boolean? = null,

    @SerialName("migrate_to_chat_id")
    val migrateToChatId: Long? = null,

    @SerialName("migrate_from_chat_id")
    val migrateFromChatId: Long? = null,

    @SerialName("pinned_message")
    val pinnedMessage: Message? = null,

    @SerialName("invoice")
    val invoice: Invoice? = null,

    @SerialName("successful_payment")
    val successfulPayment: SuccessfulPayment? = null,

    @SerialName("connected_website")
    val connectedWebsite: String? = null,

    @SerialName("passport_data")
    val passportData: PassportData? = null,

    @SerialName("reply_markup")
    val replyMarkup: InlineKeyboardMarkup? = null,
)