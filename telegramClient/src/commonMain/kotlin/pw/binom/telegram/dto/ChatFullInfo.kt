package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object contains full information about a chat.
 *
 * `getChat` returns this; the slim [Chat] only shows up inline on incoming messages.
 */
@Serializable
data class ChatFullInfo(
    val id: Long,
    val type: ChatType,
    val title: String? = null,
    val username: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,

    /**
     * Optional. True, if the chat is a forum (has topics).
     */
    @SerialName("is_forum")
    val isForum: Boolean? = null,

    @SerialName("accent_color_id")
    val accentColorId: Int? = null,
    @SerialName("max_reaction_count")
    val maxReactionCount: Int? = null,

    val photo: ChatPhoto? = null,
    val description: String? = null,

    @SerialName("active_usernames")
    val activeUsernames: List<String>? = null,

    @SerialName("available_reactions")
    val availableReactions: List<ReactionType>? = null,

    @SerialName("bio")
    val bio: String? = null,

    @SerialName("birthdate")
    val birthdate: Birthdate? = null,

    @SerialName("business_intro")
    val businessIntro: BusinessIntro? = null,

    @SerialName("business_location")
    val businessLocation: BusinessLocation? = null,

    @SerialName("business_opening_hours")
    val businessOpeningHours: BusinessOpeningHours? = null,

    @SerialName("personal_chat")
    val personalChat: Chat? = null,

    @SerialName("linked_chat")
    val linkedChat: Chat? = null,

    @SerialName("location")
    val location: ChatLocation? = null,

    @SerialName("can_send_paid_media")
    val canSendPaidMedia: Boolean? = null,

    @SerialName("has_main_web_app")
    val hasMainWebApp: Boolean? = null,

    @SerialName("has_private_forwards")
    val hasPrivateForwards: Boolean? = null,

    @SerialName("has_hidden_members")
    val hasHiddenMembers: Boolean? = null,

    @SerialName("has_protected_content")
    val hasProtectedContent: Boolean? = null,

    @SerialName("has_visible_history")
    val hasVisibleHistory: Boolean? = null,

    @SerialName("invite_link")
    val inviteLink: String? = null,

    val pinnedMessage: Message? = null,
    val slowModeDelay: Int? = null,
    val permissions: ChatPermissions? = null,

    @SerialName("sticker_set_name")
    val stickerSetName: String? = null,

    @SerialName("can_set_sticker_set")
    val canSetStickerSet: Boolean? = null,

    @SerialName("custom_emoji_sticker_set_name")
    val customEmojiStickerSetName: String? = null,

    @SerialName("unrestrict_boost_count")
    val unrestrictBoostCount: Int? = null,

    @SerialName("profile_accent_color_id")
    val profileAccentColorId: Int? = null,

    @SerialName("profile_background_custom_emoji_id")
    val profileBackgroundCustomEmojiId: String? = null,

    @SerialName("emoji_status_custom_emoji_id")
    val emojiStatusCustomEmojiId: String? = null,
)