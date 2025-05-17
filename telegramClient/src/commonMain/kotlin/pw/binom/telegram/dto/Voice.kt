package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Voice(
    /**
     * Identifier for this file, which can be used to download or reuse the file
     */
    @SerialName("file_id")
    val fileId: String,
    /**
     * Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
     */
    @SerialName("file_unique_id")
    val fileUniqueId: String,
    /**
     * Duration of the audio in seconds as defined by the sender
     */
    val duration: Int,
    /**
     * MIME type of the file as defined by the sender
     */
    @SerialName("mime_type")
    val mimeType: String? = null,
    /**
     * File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
     */
    @SerialName("file_size")
    val fileSize: Int? = null,
)