package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ParseMode {
    @SerialName("MarkdownV2")
    MARKDOWN_V2,

    @SerialName("HTML")
    HTML,

    @SerialName("Markdown")
    MARKDOWN
}
