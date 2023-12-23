package ru.kyamshanov.mission.authorization

import ru.kyamshanov.mission.client.models.SocialService
import java.time.LocalDateTime
import java.util.*

data class AuthorizationModel(
    val authorizationId: UUID?,
    val authorizationCode: String,
    val clientId: String,
    val scopes: String,
    val socialService: SocialService?,
    val authenticationCodeExpiresAt: LocalDateTime,
    val accessTokenExpiresAt: LocalDateTime?,
    val accessToken: String?,
    val refreshToken: String?,
    val refreshTokenExpiresAt: LocalDateTime?,
    val refreshTokenIssuedAt: LocalDateTime?,
    val metadata: Metadata?,
    val issuedAt: LocalDateTime,
    val userId: UUID?,
    val accessTokenIssuedAt: LocalDateTime?,
    val enabled: Boolean
) {
    constructor(
        socialService: SocialService,
        clientId: String,
        issuedAt: LocalDateTime,
        authorizationCode: String,
        authenticationCodeExpiresAt: LocalDateTime,
        scopes: String,
        metadata: Metadata
    ) : this(
        authorizationId = null,
        authorizationCode = authorizationCode,
        clientId = clientId,
        scopes = scopes,
        socialService = socialService,
        authenticationCodeExpiresAt = authenticationCodeExpiresAt,
        accessTokenExpiresAt = null,
        accessToken = null,
        refreshToken = null,
        refreshTokenExpiresAt = null,
        metadata = metadata,
        issuedAt = issuedAt,
        userId = null,
        refreshTokenIssuedAt = null,
        accessTokenIssuedAt = null,
        enabled = false
    )

    data class Metadata(
        val codeChallenge: String,
        val encryptedToken: String,
    )
}