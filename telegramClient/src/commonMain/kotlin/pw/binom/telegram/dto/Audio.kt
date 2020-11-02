package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents an audio file to be treated as music by the Telegram clients.
 */
@Serializable
class Audio(
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
         * Duration of the audio in seconds as defined by sender
         */
        val duration: Int,

        /**
         * Performer of the audio as defined by sender or by audio tags
         */
        val performer: String? = null,

        /**
         * Title of the audio as defined by sender or by audio tags
         */
        val title: String? = null,

        /**
         * MIME type of the file as defined by sender
         */
        @SerialName("mime_type")
        val mimeType: String? = null,

        /**
         * File size
         */
        @SerialName("file_size")
        val fileSize: Int? = null,

        /**
         * Thumbnail of the album cover to which the music file belongs
         */
        val thumb: PhotoSize? = null
)