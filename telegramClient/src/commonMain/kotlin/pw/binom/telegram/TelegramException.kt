package pw.binom.telegram

class TelegramException(val code: Int, val description: String) : AbstractTelegramException() {
    override val message: String?
        get() = "$code: $description"
}
