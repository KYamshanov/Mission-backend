package ru.kyamshanov.mission.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import kotlin.collections.set

fun Application.configureSession() {
    install(Sessions) {
        cookie<OAuthSessionConfig>("ID_SESSION") {
            cookie.maxAgeInSeconds = 120
        }
    }
}