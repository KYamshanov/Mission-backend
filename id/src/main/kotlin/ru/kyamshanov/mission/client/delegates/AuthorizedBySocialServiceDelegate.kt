package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.client.AuthorizedDelegate
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import ru.kyamshanov.mission.plugins.*
import ru.kyamshanov.mission.tables.AuthorizationMetadata
import ru.kyamshanov.mission.tables.AuthorizationTable
import java.time.LocalDateTime

class AuthorizedBySocialServiceDelegate(
    private val clientId: String,
    private val socialService: SocialService,
) : AuthorizedDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {

            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            println("Github cccess token: ${principal?.accessToken}")
            println("State: ${principal?.state}")

            val oAuthConfig = requireNotNull(call.sessions.get<OAuthSessionConfig>())
            call.sessions.clear<OAuthSessionConfig>()

            val authorizationCode = generateNewToken()

            transaction {
                AuthorizationTable.insert {
                    it[socialService] = this@AuthorizedBySocialServiceDelegate.socialService
                    it[clientId] = this@AuthorizedBySocialServiceDelegate.clientId
                    it[issuedAt] = LocalDateTime.now()
                    it[authenticationCode] = authorizationCode
                    it[authenticationCodeExpiresAt] = LocalDateTime.now().plusMinutes(5)
                    it[scopes] = oAuthConfig.scopes
                    it[authorizationMetadata] =
                        AuthorizationMetadata(oAuthConfig.codeChallenge, principal!!.accessToken)
                }
            }

            call.respondRedirect("${oAuthConfig.callbackURL}?code=${authorizationCode}")
        }

}