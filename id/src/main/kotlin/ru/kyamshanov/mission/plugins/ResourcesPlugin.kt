package ru.kyamshanov.mission.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureResourcesRouting() {
    routing {
        staticResources("/oauth2/images", "templates/images")
        staticResources("/oauth2/css", "templates/css")
    }
}