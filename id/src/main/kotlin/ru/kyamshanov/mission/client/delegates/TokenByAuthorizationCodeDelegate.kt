package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.GetTokenDelegate
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.security.SimpleCipher
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

class TokenByAuthorizationCodeDelegate(
    private val clientFactory: ClientFactory,
    private val formParameters: Parameters,
    private val tokenCipher: SimpleCipher,
    private val authorizationRepository: AuthorizationRepository,
) : GetTokenDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            call.request.headers
            val authCode = checkNotNull(formParameters["code"]) { "the code parameter required" }
            val codeVerifier = checkNotNull(formParameters["code_verifier"]) { "the code_verifier parameter required" }

            val authorizationModel = authorizationRepository.findFirstByAuthorizationCode(authCode)
            checkNotNull(authorizationModel.metadata) { "Auth metadata has not been established" }
            checkNotNull(authorizationModel.socialService) { "Social service has not been established" }
            checkNotNull(authorizationModel.authorizationId) { "Authorization id has not been established" }

            val clientId: String = authorizationModel.clientId
            val codeChallenge: String = authorizationModel.metadata.codeChallenge
            val token: String = authorizationModel.metadata.encryptedToken.let { token -> tokenCipher.decrypt(token) }
            val scopes: String = authorizationModel.scopes
            val socialService: SocialService = authorizationModel.socialService
            val authenticationCodeExpiresAt: LocalDateTime = authorizationModel.authenticationCodeExpiresAt

            check(LocalDateTime.now() < authenticationCodeExpiresAt) { "Authorization code has been expired" }
            check(getCodeChallenge(codeVerifier) == codeChallenge) { "Code challenge verification failed" }

            val client = clientFactory.create(clientId).getOrThrow()
                .also { it.clientAuthorization().getOrThrow().execute(pipeline, httpClient) }
            val userId = client.identificationServices[socialService]!!.identify(token)


            val response = client.generateJwtTokens(userId, scopes)
                .let { (accessToken, accessTokenExpiresAt, refreshToken, refreshTokenExpiresAt) ->
                    authorizationModel.copy(
                        userId = userId,
                        accessTokenIssuedAt = LocalDateTime.now(),
                        accessTokenExpiresAt = accessTokenExpiresAt,
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        refreshTokenExpiresAt = refreshTokenExpiresAt,
                        refreshTokenIssuedAt = LocalDateTime.now(),
                        enabled = true
                    )
                }
                .let {
                    checkNotNull(it.accessToken)

                    authorizationRepository.updateAuthData(it)
                    TokensRsDto(
                        accessToken = it.accessToken,
                        refreshToken = it.refreshToken?.let { token -> tokenCipher.encrypt(token) },
                        scope = scopes,
                        tokenType = "Bearer",
                        expiresIn = client.accessTokenLifetimeInMS / 1000
                    )
                }

            call.respond(response)
        }

    private fun getCodeChallenge(codeVerifier: String): String {
        val bytes: ByteArray = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
}