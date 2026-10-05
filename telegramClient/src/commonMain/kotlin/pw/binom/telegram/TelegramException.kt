package pw.binom.telegram

class TelegramException(val code: Int, val description: String) : RuntimeException() {
    override val message: String
        get() = "$code: $description"
}
