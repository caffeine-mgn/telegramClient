package pw.binom.telegram.dto

import kotlinx.serialization.Serializable

/**
 * Birthdate of a user. See <https://core.telegram.org/bots/api#birthdate>.
 */
@Serializable
data class Birthdate(
    val day: Int,
    val month: Int,
    val year: Int? = null,
)

/**
 * Location to which a business is connected. See <https://core.telegram.org/bots/api#businesslocation>.
 */
@Serializable
data class BusinessLocation(
    val address: String,
    val location: Location? = null,
)

/**
 * Intro of a business. See <https://core.telegram.org/bots/api#businessintro>.
 */
@Serializable
data class BusinessIntro(
    val title: String? = null,
    val message: String? = null,
    val sticker: Sticker? = null,
)

/**
 * Opening hours of a business. See <https://core.telegram.org/bots/api#businessopeninghours>.
 */
@Serializable
data class BusinessOpeningHours(
    val time_zone_name: String,
    val opening_hours: List<BusinessOpeningHoursInterval>,
)

@Serializable
data class BusinessOpeningHoursInterval(
    val minute: Int,
    val from: String,
    val to: String,
)

/**
 * Location to which a chat is connected. See <https://core.telegram.org/bots/api#chatlocation>.
 */
@Serializable
data class ChatLocation(
    val location: Location,
    val address: String,
)