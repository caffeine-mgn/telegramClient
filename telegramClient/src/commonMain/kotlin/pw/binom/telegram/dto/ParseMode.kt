package pw.binom.telegram.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ParseMode(val code: String) {
    @SerialName("MarkdownV2")
    MARKDOWN_V2("MarkdownV2"),

    @SerialName("HTML")
    HTML("HTML"),

    @SerialName("Markdown")
    MARKDOWN("Markdown"),
}
