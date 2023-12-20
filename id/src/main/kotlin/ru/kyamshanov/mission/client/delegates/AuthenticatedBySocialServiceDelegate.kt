package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.authorization.AuthorizationModel
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.client.AuthenticationGrantedDelegate
import ru.kyamshanov.mission.client.issuer.TokenIssuer
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import ru.kyamshanov.mission.security.SimpleCipher
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MILLIS

class AuthenticatedBySocialServiceDelegate(
    private val clientId: String,
    private val socialService: SocialService,
    private val authenticationCodeLifetimeInMs: Long,
    private val tokenCipher: SimpleCipher,
    private val tokenIssuer: TokenIssuer,
    private val authorizationRepository: AuthorizationRepository
) : AuthenticationGrantedDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            val oAuthConfig = checkNotNull(call.sessions.get<OAuthSessionConfig>())

            call.sessions.clear<OAuthSessionConfig>()

            val authorizationCode = tokenIssuer.generateToken()
            val authorizationModel = AuthorizationModel(
                socialService = this@AuthenticatedBySocialServiceDelegate.socialService,
                clientId = this@AuthenticatedBySocialServiceDelegate.clientId,
                issuedAt = LocalDateTime.now(),
                authorizationCode = authorizationCode,
                authenticationCodeExpiresAt = LocalDateTime.now().plus(authenticationCodeLifetimeInMs, MILLIS),
                scopes = oAuthConfig.scopes,
                metadata = AuthorizationModel.Metadata(
                    codeChallenge = oAuthConfig.codeChallenge,
                    encryptedToken = tokenCipher.encrypt(principal!!.accessToken)
                ),
            )

            authorizationRepository.insert(authorizationModel)

            call.respondRedirect("${oAuthConfig.callbackURL}?code=${authorizationCode}&state=${oAuthConfig.state}")
        }

}