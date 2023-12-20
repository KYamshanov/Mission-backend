package ru.kyamshanov.mission.client

import ru.kyamshanov.mission.client.models.JwtTokenPair
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.identification.IdentificationService
import java.util.*

interface Client {

    val clientId: String

    val identificationServices: Map<SocialService, IdentificationService>

    val accessTokenLifetimeInMS: Long

    val refreshTokenLifetimeInMS: Long

    val isRefreshTokenSupported: Boolean

    val authenticationCodeLifeTimeInMs: Long

    /**
     * process to authorize user
     * @return delegate to intercept ktor pipeline
     */
    fun authorize(responseType: String, scope: String, state: String): Result<AuthorizeDelegate>

    /**
     * process user authorized by social service [service]
     * @return the delegate to process the user authorization
     */
    fun authorizedBy(service: SocialService): Result<AuthenticationGrantedDelegate>

    fun clientAuthorization(): Result<ClientAuthorizationDelegate>

    /**
     * create new access token for user with id [userId] and scope credentials [scopes]
     */
    fun generateJwtTokens(userId: UUID, scopes: String): JwtTokenPair
}