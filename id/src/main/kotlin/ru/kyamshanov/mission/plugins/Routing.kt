package ru.kyamshanov.mission.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/index") {
            call.respondTemplate("login.ftl")
        }
    }
}
