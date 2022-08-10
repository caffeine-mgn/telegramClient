package pw.binom.telegram.dto

import kotlinx.serialization.Serializable

@Serializable
class Response<T>(
    val ok: Boolean,
    val result: T
)
