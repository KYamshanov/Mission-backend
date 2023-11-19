package ru.kyamshanov.mission.dto

import kotlinx.serialization.Serializable

@Serializable
data class OAuthSessionConfig(
    val codeChallenge: String,
    val scopes: String,
    val callbackURL: String,
    val clientId: String,
    val state: String,
    val csrfToken: String,
)