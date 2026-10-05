package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Reply-target parameters for a message.
 *
 * See <https://core.telegram.org/bots/api#replyparameters>.
 */
@Serializable
data class ReplyParameters(
    @SerialName("message_id")
    val messageId: Long,

    @SerialName("chat_id")
    val chatId: String? = null,

    @SerialName("allow_sending_without_reply")
    val allowSendingWithoutReply: Boolean? = null,

    @SerialName("quote")
    val quote: String? = null,

    @SerialName("quote_parse_mode")
    val quoteParseMode: ParseMode? = null,

    @SerialName("quote_entities")
    val quoteEntities: List<MessageEntity>? = null,

    @SerialName("quote_position")
    val quotePosition: Int? = null,

    @SerialName("checklist_task_id")
    val checklistTaskId: Long? = null,
)

/**
 * Rights of an administrator in a chat. Used by [setMyDefaultAdministratorRights] and embedded
 * in the per-admin [ChatMember.Administrator] response.
 */
@Serializable
data class ChatAdministratorRights(
    @SerialName("is_anonymous")
    val isAnonymous: Boolean = false,

    @SerialName("can_manage_chat")
    val canManageChat: Boolean = false,

    @SerialName("can_delete_messages")
    val canDeleteMessages: Boolean = false,

    @SerialName("can_manage_video_chats")
    val canManageVideoChats: Boolean = false,

    @SerialName("can_restrict_members")
    val canRestrictMembers: Boolean = false,

    @SerialName("can_promote_members")
    val canPromoteMembers: Boolean = false,

    @SerialName("can_change_info")
    val canChangeInfo: Boolean = false,

    @SerialName("can_invite_users")
    val canInviteUsers: Boolean = false,

    @SerialName("can_post_messages")
    val canPostMessages: Boolean = false,

    @SerialName("can_edit_messages")
    val canEditMessages: Boolean = false,

    @SerialName("can_pin_messages")
    val canPinMessages: Boolean = false,

    @SerialName("can_manage_topics")
    val canManageTopics: Boolean = false,

    @SerialName("can_post_stories")
    val canPostStories: Boolean = false,

    @SerialName("can_edit_stories")
    val canEditStories: Boolean = false,

    @SerialName("can_delete_stories")
    val canDeleteStories: Boolean = false,
)