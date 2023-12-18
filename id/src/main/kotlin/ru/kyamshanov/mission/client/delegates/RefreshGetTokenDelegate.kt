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
import ru.kyamshanov.mission.client.GetTokenDelegate
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.plugins.generateRefreshToken
import ru.kyamshanov.mission.plugins.keyPair
import ru.kyamshanov.mission.plugins.kid
import ru.kyamshanov.mission.tables.AuthorizationTable
import ru.kyamshanov.mission.tables.UsersTable
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class RefreshGetTokenDelegate(
    private val issuerUrl: String,
    private val clientFactory: ClientFactory,
    private val formParameters: Parameters,
) : GetTokenDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val refreshToken: String = requireNotNull(formParameters["refresh_token"])
            val clientId: String
            val scopes: String
            val userId: UUID
            val refreshTokenExpiresAt: LocalDateTime
            val authorizationId: UUID


            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.refreshTokenValue eq refreshToken
                }.limit(1).single()
            }.also {
                clientId = it[AuthorizationTable.clientId]
                scopes = it[AuthorizationTable.scopes]
                userId = it[AuthorizationTable.userId]
                refreshTokenExpiresAt = it[AuthorizationTable.refreshTokenExpiresAt]
                authorizationId = it[AuthorizationTable.id].value
            }

            if (refreshTokenExpiresAt < LocalDateTime.now()) throw IllegalStateException("RefreshToken is expired")

            transaction {
                UsersTable.select { UsersTable.id eq userId }.single().let { it[UsersTable.enabled] }
            }.also {
                if (it.not()) throw IllegalStateException("User is disabled")
            }

            val client = clientFactory.create(clientId).getOrThrow()
                .also { it.clientAuthorization().getOrThrow().execute(pipeline, httpClient) }
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
            val newRefreshTokenExpiresAt: Date?
            val newRefreshToken: String?

            if (client.isRefreshTokenSupported) {
                newRefreshToken = generateRefreshToken()
                newRefreshTokenExpiresAt = Date(System.currentTimeMillis() + client.refreshTokenLifetimeInMS)
            } else {
                newRefreshToken = null
                newRefreshTokenExpiresAt = null
            }

            transaction {
                AuthorizationTable.update({ AuthorizationTable.id eq authorizationId }) {
                    it[issuedAt] = LocalDateTime.now()
                    it[AuthorizationTable.accessTokenExpiresAt] = accessTokenExpiresAt
                    it[accessTokenValue] = accessToken
                    if (newRefreshToken != null) {
                        it[refreshTokenValue] = newRefreshToken
                        it[refreshTokenIssuedAt] = LocalDateTime.now()
                    }
                    if (newRefreshTokenExpiresAt != null) it[AuthorizationTable.refreshTokenExpiresAt] =
                        LocalDateTime.ofInstant(newRefreshTokenExpiresAt.toInstant(), ZoneId.systemDefault())
                }.also {
                    if (it != 1) throw IllegalStateException("There is was updated not one entity in the database at refreshing. [$it]")
                }
            }
            val response = TokensRsDto(
                accessToken = accessToken,
                refreshToken = newRefreshToken,
                scope = scopes,
                tokenType = "Bearer",
                expiresIn = client.accessTokenLifetimeInMS / 1000
            )
            call.respond(response)
        }

}