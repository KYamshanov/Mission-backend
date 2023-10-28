package ru.kyamshanov.mission.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    routing {
        get("/index") {
            call.respondTemplate("login.ftl")
        }


        authenticate("auth-oauth-github") {
            get("/login") {

            }

            get("/desktop/authorized") {

            }
        }
    }
}
