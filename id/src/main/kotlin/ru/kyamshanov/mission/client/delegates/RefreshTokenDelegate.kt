package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.authorization.UserRepository
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.GetTokenDelegate
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.security.SimpleCipher
import java.time.LocalDateTime
import java.util.*

/**
 * delegate to refresh tokens
 */
class RefreshTokenDelegate(
    private val clientFactory: ClientFactory,
    private val formParameters: Parameters,
    private val authorizationRepository: AuthorizationRepository,
    private val userRepository: UserRepository,
    private val simpleCipher: SimpleCipher,
) : GetTokenDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val refreshToken: String = requireNotNull(formParameters["refresh_token"]).let { simpleCipher.decrypt(it) }

            val authorizationModel = authorizationRepository.findFirstByRefreshToken(refreshToken)
            check(authorizationModel.enabled) { "Authorization has been disabled" }

            val clientId: String = authorizationModel.clientId
            val scopes: String = authorizationModel.scopes
            val userId: UUID = checkNotNull(authorizationModel.userId) { "The userId required to the refresh token" }
            val refreshTokenExpiresAt: LocalDateTime =
                checkNotNull(authorizationModel.refreshTokenExpiresAt) { "The refreshTokenExpiresAt required to the refresh token" }

            if (refreshTokenExpiresAt < LocalDateTime.now()) throw IllegalStateException("RefreshToken is expired")

            userRepository.findUserById(userId).also {
                check(it.enabled) { "User is disabled" }
            }

            val client = clientFactory.create(clientId).getOrThrow()
                .also { it.clientAuthorization().getOrThrow().execute(pipeline, httpClient) }

            val newAccessToken: String
            val newRefreshToken: String?

            client.generateJwtTokens(userId, scopes)
                .let { (accessToken, accessTokenExpiresAt, refreshToken, refreshTokenExpiresAt) ->
                    newAccessToken = accessToken
                    newRefreshToken = refreshToken
                    authorizationModel.copy(
                        accessTokenIssuedAt = LocalDateTime.now(),
                        accessTokenExpiresAt = accessTokenExpiresAt,
                        accessToken = accessToken,
                        refreshToken = newRefreshToken,
                        refreshTokenExpiresAt = refreshTokenExpiresAt,
                        refreshTokenIssuedAt = LocalDateTime.now()
                    )
                }
                .also {
                    authorizationRepository.updateTokens(it)
                }

            val response = TokensRsDto(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken?.let { simpleCipher.encrypt(it) },
                scope = scopes,
                tokenType = "Bearer",
                expiresIn = client.accessTokenLifetimeInMS / 1000
            )
            call.respond(response)
        }

}