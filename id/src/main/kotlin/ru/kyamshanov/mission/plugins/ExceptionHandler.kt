package ru.kyamshanov.mission.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ru.kyamshanov.mission.exception.ProcessingException

fun Application.exceptionHandler() = install(StatusPages) {
    exception<Throwable> { call, cause ->
        cause.printStackTrace()
        when (cause) {
            is ProcessingException -> call.respond(cause.responseCode)
            is IllegalStateException -> call.respond(HttpStatusCode.NotFound)
        }
    }
}