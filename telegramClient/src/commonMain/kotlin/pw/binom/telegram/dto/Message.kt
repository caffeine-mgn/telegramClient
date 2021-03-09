package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("message_id")
    val messageId: Long,
    val from: User? = null,
    val date: Long,
    val chat: Chat,
    @SerialName("forward_from")
    val forwardFrom: User? = null,

    @SerialName("forward_from_chat")
    val forwardFromChat: Chat? = null,

    @SerialName("forward_from_message_id")
    val forwardFromMessage_id: Long? = null,

    @SerialName("forward_signature")
    val forwardSignature: String? = null,

    @SerialName("forward_sender_name")
    val forwardSenderName: String? = null,

    @SerialName("forward_date")
    val forwardDate: Long? = null,

    @SerialName("reply_to_message")
    val replyToMessage: Message? = null,

    val via_bot: User? = null,
    val edit_date: Long? = null,
    @SerialName("media_group_id")
    val mediaGroupId: String? = null,

    @SerialName("author_signature")
    val authorSignature: String? = null,

    val text: String? = null,
    val entities: List<MessageEntity>? = null,
    val animation: Animation? = null,
    val audio: Audio? = null,
    val document: Document? = null,
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

    @SerialName("pinned_message")
    val pinnedMessage: Message? = null,

    val invoice: Invoice? = null,

    @SerialName("successful_payment")
    val successfulPayment: SuccessfulPayment? = null,

    @SerialName("connected_website")
    val connectedWebsite: String? = null,

    @SerialName("passport_data")
    val passportData: PassportData? = null,

    @SerialName("reply_markup")
    val replyMarkup: InlineKeyboardMarkup? = null
)