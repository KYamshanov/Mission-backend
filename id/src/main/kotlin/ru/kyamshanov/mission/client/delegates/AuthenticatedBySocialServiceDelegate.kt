package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.AuthenticationGrantedDelegate
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import ru.kyamshanov.mission.plugins.generateNewToken
import ru.kyamshanov.mission.security.SimpleCipher
import ru.kyamshanov.mission.tables.AuthorizationMetadata
import ru.kyamshanov.mission.tables.AuthorizationTable
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MILLIS

class AuthenticatedBySocialServiceDelegate(
    private val clientId: String,
    private val socialService: SocialService,
    private val authenticationCodeLifetimeInMs: Long,
    private val tokenCipher: SimpleCipher
) : AuthenticationGrantedDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            val oAuthConfig = checkNotNull(call.sessions.get<OAuthSessionConfig>())

            call.sessions.clear<OAuthSessionConfig>()

            val authorizationCode = generateNewToken()

            transaction {
                AuthorizationTable.insert {
                    it[socialService] = this@AuthenticatedBySocialServiceDelegate.socialService
                    it[clientId] = this@AuthenticatedBySocialServiceDelegate.clientId
                    it[issuedAt] = LocalDateTime.now()
                    it[authenticationCode] = authorizationCode
                    it[authenticationCodeExpiresAt] = LocalDateTime.now().plus(authenticationCodeLifetimeInMs, MILLIS)
                    it[scopes] = oAuthConfig.scopes
                    it[authorizationMetadata] = AuthorizationMetadata(
                        codeChallenge = oAuthConfig.codeChallenge,
                        token = tokenCipher.encrypt(principal!!.accessToken)
                    )
                }
            }

            call.respondRedirect("${oAuthConfig.callbackURL}?code=${authorizationCode}&state=${oAuthConfig.state}")
        }

}