package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents a chat.
 */
@Serializable
data class Chat(
    /**
     * Unique identifier for this chat. This number may be greater than 32 bits and some programming languages may have difficulty/silent defects in interpreting it. But it is smaller than 52 bits, so a signed 64 bit integer or double-precision float type are safe for storing this identifier.
     */
    val id: Long,

    /**
     * Type of chat, can be either “private”, “group”, "supergroup" or “channel”
     */
    val type: String,

    /**
     * Title, for supergroups, channels and group chats
     */
    val title: String? = null,

    /**
     * Username, for private chats, supergroups and channels if available
     */
    val username: String? = null,

    /**
     * First name of the other party in a private chat
     */
    @SerialName("first_name")
    val firstName: String? = null,

    /**
     * Last name of the other party in a private chat
     */
    @SerialName("last_name")
    val lastName: String? = null,

    /**
     * Chat photo. Returned only in getChat.
     */
    val photo: ChatPhoto? = null,

    /**
     * Description, for groups, supergroups and channel chats. Returned only in getChat.
     */
    val description: String? = null,

    /**
     * Chat invite link, for groups, supergroups and channel chats. Each administrator in a chat generates their own invite links, so the bot must first generate the link using exportChatInviteLink. Returned only in getChat.
     */
    @SerialName("invite_link")
    val inviteLink: String? = null,

    /**
     * Pinned message, for groups, supergroups and channels. Returned only in getChat.
     */
    @SerialName("pinned_message")
    val pinnedMessage: Message? = null,

    /**
     * Default chat member permissions, for groups and supergroups. Returned only in getChat.
     */
    val permissions: ChatPermissions? = null,

    /**
     * For supergroups, the minimum allowed delay between consecutive messages sent by each unpriviledged user. Returned only in getChat.
     */
    @SerialName("slow_mode_delay")
    val slowModeDelay: Int? = null,

    /**
     * For supergroups, name of group sticker set. Returned only in getChat.
     */
    @SerialName("sticker_set_name")
    val stickerSetName: String? = null,

    /**
     * if the bot can change the group sticker set. Returned only in getChat.
     */
    @SerialName("can_set_sticker_set")
    val canSetStickerSet: Boolean? = null
)
