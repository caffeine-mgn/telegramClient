package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object represents an animation file (GIF or H.264/MPEG-4 AVC video without sound).
 */
@Serializable
class Animation(
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
         * Video width as defined by sender
         */
        val width: Int,

        /**
         * Video height as defined by sender
         */
        val height: Int,

        /**
         * Duration of the video in seconds as defined by sender
         */
        val duration: Int,

        /**
         * Animation thumbnail as defined by sender
         */
        val thumb: PhotoSize? = null,

        /**
         * Original animation filename as defined by sender
         */
        @SerialName("file_name")
        val fileName: String? = null,

        /**
         * MIME type of the file as defined by sender
         */
        @SerialName("mime_type")
        val mimeType: String? = null,

        /**
         * File size
         */
        @SerialName("file_size")
        val fileSize: Int? = null
)