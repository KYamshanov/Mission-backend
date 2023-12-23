package ru.kyamshanov.mission.exception

import io.ktor.http.*

class ProcessingException(
    val responseCode: HttpStatusCode,
    override val message: String
) : IllegalStateException()

inline fun verify(value: Boolean, lazyMessage: () -> Pair<HttpStatusCode, Any>) {
    if (!value) {
        val (code, message) = lazyMessage()
        throw ProcessingException(code, message.toString())
    }
}