package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Polymorphic description of a member's status in a chat. Telegram returns the right concrete
 * type depending on the [status] field. The serializer is custom and picks the right shape.
 *
 * See <https://core.telegram.org/bots/api#chatmember>.
 */
@Serializable(with = ChatMemberSerializer::class)
sealed class ChatMember {
    abstract val user: User
    abstract val status: String
}

@Serializable
data class ChatMemberOwner(
    override val user: User,
    override val status: String = "creator",
    @SerialName("is_anonymous") val isAnonymous: Boolean = false,
    @SerialName("custom_title") val customTitle: String? = null,
) : ChatMember()

@Serializable
data class ChatMemberAdministrator(
    override val user: User,
    override val status: String = "administrator",
    @SerialName("can_be_edited") val canBeEdited: Boolean? = null,
    @SerialName("is_anonymous") val isAnonymous: Boolean? = null,
    @SerialName("can_manage_chat") val canManageChat: Boolean? = null,
    @SerialName("can_delete_messages") val canDeleteMessages: Boolean? = null,
    @SerialName("can_manage_video_chats") val canManageVideoChats: Boolean? = null,
    @SerialName("can_restrict_members") val canRestrictMembers: Boolean? = null,
    @SerialName("can_promote_members") val canPromoteMembers: Boolean? = null,
    @SerialName("can_change_info") val canChangeInfo: Boolean? = null,
    @SerialName("can_invite_users") val canInviteUsers: Boolean? = null,
    @SerialName("can_pin_messages") val canPinMessages: Boolean? = null,
    @SerialName("can_post_stories") val canPostStories: Boolean? = null,
    @SerialName("can_edit_stories") val canEditStories: Boolean? = null,
    @SerialName("can_delete_stories") val canDeleteStories: Boolean? = null,
    @SerialName("can_manage_topics") val canManageTopics: Boolean? = null,
    @SerialName("can_post_messages") val canPostMessages: Boolean? = null,
    @SerialName("can_edit_messages") val canEditMessages: Boolean? = null,
    @SerialName("custom_title") val customTitle: String? = null,
) : ChatMember()

@Serializable
data class ChatMemberMember(
    override val user: User,
    override val status: String = "member",
    @SerialName("until_date") val untilDate: Long? = null,
) : ChatMember()

@Serializable
data class ChatMemberRestricted(
    override val user: User,
    override val status: String = "restricted",
    @SerialName("is_member") val isMember: Boolean,
    @SerialName("until_date") val untilDate: Long? = null,
    val permissions: ChatPermissions? = null,
    @SerialName("can_send_paid_media") val canSendPaidMedia: Boolean? = null,
) : ChatMember()

@Serializable
data class ChatMemberLeft(
    override val user: User,
    override val status: String = "left",
) : ChatMember()

@Serializable
data class ChatMemberBanned(
    override val user: User,
    override val status: String = "kicked",
    @SerialName("until_date") val untilDate: Long = 0,
) : ChatMember()