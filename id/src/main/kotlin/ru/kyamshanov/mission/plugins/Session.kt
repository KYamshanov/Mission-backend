package ru.kyamshanov.mission.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import ru.kyamshanov.mission.dto.OAuthSessionConfig

fun Application.configureSession() {
    install(Sessions) {
        cookie<OAuthSessionConfig>("ID_SESSION",SessionStorageMemory()) {
            cookie.maxAgeInSeconds = 120
        }
    }
}