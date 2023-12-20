package ru.kyamshanov.mission.client.models

import java.time.LocalDateTime

data class JwtTokenPair(
    val accessToken: String,
    val accessTokenExpiresAt: LocalDateTime,
    val refreshToken: String?,
    val refreshTokenExpiresAt: LocalDateTime?
)
