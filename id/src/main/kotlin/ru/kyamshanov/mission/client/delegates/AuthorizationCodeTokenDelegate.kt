package ru.kyamshanov.mission.client.delegates

import io.jsonwebtoken.Jwts
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.plugins.generateRefreshToken
import ru.kyamshanov.mission.plugins.getCodeChallenge
import ru.kyamshanov.mission.plugins.keyPair
import ru.kyamshanov.mission.plugins.kid
import ru.kyamshanov.mission.tables.AuthorizationTable
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class AuthorizationCodeTokenDelegate(
    private val issuerUrl: String,
    private val clientFactory: ClientFactory,
    private val formParameters: Parameters,
) : TokenDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            call.request.headers
            val authCode: String = checkNotNull(formParameters["code"]) { "the code parameter required" }
            val codeVerifier: String = checkNotNull(formParameters["the code_verifier parameter required"])
            val clientId: String
            val codeChallenge: String
            val token: String
            val scopes: String
            val socialService: SocialService
            val authorizationId: UUID
            val authenticationCodeExpiresAt: LocalDateTime

            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.authenticationCode eq authCode
                }.limit(1).single()
            }.also {
                clientId = it[AuthorizationTable.clientId]
                codeChallenge = it[AuthorizationTable.authorizationMetadata].codeChallenge
                token = requireNotNull(it[AuthorizationTable.authorizationMetadata].token)
                scopes = it[AuthorizationTable.scopes]
                socialService = it[AuthorizationTable.socialService]
                authorizationId = it[AuthorizationTable.id].value
                authenticationCodeExpiresAt = it[AuthorizationTable.authenticationCodeExpiresAt]
            }

            check(LocalDateTime.now() < authenticationCodeExpiresAt) { "Authorization code has been expired" }
            check(getCodeChallenge(codeVerifier) == codeChallenge) { "Code challenge verification failed" }

            val client = clientFactory.create(clientId).getOrThrow().also {
                //there will be verification client secret
            }
            val userId = client.identificationServices[socialService]!!.identify(token)

            //Generation access and refresh token

            val accessTokenExpiresAt = LocalDateTime.now().plus(client.accessTokenLifetimeInMS, ChronoUnit.MILLIS)
            val accessToken = Jwts.builder()
                .header()
                .keyId(kid)
                .and()
                .subject(userId.toString())
                .audience().add(client.clientId)
                .and()
                .claim("scope", scopes)
                .claim("iss", issuerUrl)
                .claim("exp", Timestamp.valueOf(accessTokenExpiresAt).time)
                .signWith(keyPair.private)
                .compact()
            val refreshTokenExpiresAt: Date?
            val refreshToken: String?

            if (client.isRefreshTokenSupported) {
                refreshToken = generateRefreshToken()
                refreshTokenExpiresAt = Date(System.currentTimeMillis() + client.refreshTokenLifetimeInMS)
            } else {
                refreshToken = null
                refreshTokenExpiresAt = null
            }

            //Saving
            transaction {
                AuthorizationTable.update({ AuthorizationTable.id eq authorizationId }) {
                    it[AuthorizationTable.userId] = userId
                    it[issuedAt] = LocalDateTime.now()
                    it[AuthorizationTable.accessTokenExpiresAt] = accessTokenExpiresAt
                    it[accessTokenValue] = accessToken
                    if (refreshToken != null) {
                        it[refreshTokenValue] = refreshToken
                        it[refreshTokenIssuedAt] = LocalDateTime.now()
                    }
                    if (refreshTokenExpiresAt != null) it[AuthorizationTable.refreshTokenExpiresAt] =
                        LocalDateTime.ofInstant(refreshTokenExpiresAt.toInstant(), ZoneId.systemDefault())
                }.also {
                    if (it != 1) throw IllegalStateException("There is was updated not one entity in the database at obtaining token. [$it]")
                }
            }
            val response = TokensRsDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
                scope = scopes,
                tokenType = "Bearer",
                expiresIn = client.accessTokenLifetimeInMS / 1000
            )
            call.respond(response)
        }

}