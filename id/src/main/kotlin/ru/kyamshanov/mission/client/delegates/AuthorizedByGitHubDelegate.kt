package ru.kyamshanov.mission.client.delegates

import io.jsonwebtoken.Jwts
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
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.dto.GithubUserRsDto
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.plugins.*
import ru.kyamshanov.mission.tables.AuthorizationTable
import java.time.LocalDateTime

class AuthorizedByGitHubDelegate(
    private val issuerUrl: String
) : TokenDelegate {
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
                    it[service] = "github"
                    it[authCode] = authorizationCode
                    it[scopes] = oAuthConfig.scopes
                    it[serviceMetadata] = ServiceMetadata(token = principal?.accessToken)
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                    it[codeChallenge] = oAuthConfig.codeChallenge
                    it[enabled] = true
                }
            }

            call.respondRedirect("${oAuthConfig.callbackURL}?code=${authorizationCode}")
        }

}