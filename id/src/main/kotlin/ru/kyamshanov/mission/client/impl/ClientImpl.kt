package ru.kyamshanov.mission.client.impl

import io.jsonwebtoken.Jwts
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.client.AuthenticationGrantedDelegate
import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.ClientAuthorizationDelegate
import ru.kyamshanov.mission.client.delegates.AuthenticatedBySocialServiceDelegate
import ru.kyamshanov.mission.client.delegates.BasicClientAuthorizationDelegate
import ru.kyamshanov.mission.client.delegates.CodeAuthorizeDelegate
import ru.kyamshanov.mission.client.issuer.JwtSigner
import ru.kyamshanov.mission.client.issuer.TokenIssuer
import ru.kyamshanov.mission.client.models.*
import ru.kyamshanov.mission.identification.IdentificationServiceFactory
import ru.kyamshanov.mission.security.SimpleCipher
import java.security.SecureRandom
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

data class ClientImpl(
    override val clientId: String,
    private val authorizationGrantTypes: List<AuthorizationGrantTypes>,
    private val authorizationResponseTypes: List<ResponseType>,
    private val scopes: List<Scope>,
    private val redirectUrl: String,
    private val socialServices: List<SocialService>,
    private val identificationServiceFactory: IdentificationServiceFactory,
    override val accessTokenLifetimeInMS: Long,
    override val refreshTokenLifetimeInMS: Long,
    override val authenticationCodeLifeTimeInMs: Long,
    private val rawClientAuthenticationMethod: String?,
    private val clientSecret: String,
    private val tokenCipher: SimpleCipher,
    private val jwtSigner: JwtSigner,
    private val tokenIssuer: TokenIssuer,
    private val authorizationRepository: AuthorizationRepository,
    private val authenticationMethods: Set<AuthenticationMethod>
) : Client {

    override val identificationServices = socialServices.associateWith { identificationServiceFactory.create(it) }
    override val isRefreshTokenSupported: Boolean =
        authorizationGrantTypes.contains(AuthorizationGrantTypes.REFRESH_TOKEN)

    private val clientAuthenticationMethod: ClientAuthenticationMethod? = when (rawClientAuthenticationMethod) {
        "basic" -> ClientAuthenticationMethod.BASIC
        null -> null
        else -> throw IllegalStateException("Client authentication method is invalid")
    }

    override fun authorize(responseType: String, scope: String, state: String): Result<AuthorizeDelegate> =
        runCatching {
            validateScope(scope)

            when (authorizationResponseTypes.find { it.stringValue == responseType }) {
                ResponseType.CODE -> CodeAuthorizeDelegate(
                    clientId = clientId,
                    scope = scope,
                    clientRedirectUrl = redirectUrl,
                    state = state,
                    csrfToken = tokenIssuer.generateToken(),
                    authenticationMethods = authenticationMethods
                )

                ResponseType.TOKEN -> TODO("Authorization by token is not supported now")
                null -> throw IllegalStateException("Authorization response types is invalid")
            }
        }

    override fun authorizedBy(service: SocialService): Result<AuthenticationGrantedDelegate> = runCatching {
        AuthenticatedBySocialServiceDelegate(
            clientId = clientId,
            socialService = service,
            authenticationCodeLifetimeInMs = authenticationCodeLifeTimeInMs,
            tokenCipher = tokenCipher,
            tokenIssuer = tokenIssuer,
            authorizationRepository = authorizationRepository
        )
    }

    override fun clientAuthorization(): Result<ClientAuthorizationDelegate> = runCatching {
        when (clientAuthenticationMethod) {
            ClientAuthenticationMethod.BASIC -> BasicClientAuthorizationDelegate(
                clientId = clientId,
                clientSecret = clientSecret
            )

            null -> throw IllegalStateException("ClientAuthenticationMethod not specified")
        }
    }

    private val secureRandom: SecureRandom = SecureRandom()
    private val base64Encoder: Base64.Encoder = Base64.getUrlEncoder()

    override fun generateJwtTokens(userId: UUID, scopes: String): JwtTokenPair {
        val accessTokenExpiresAt = LocalDateTime.now().plus(accessTokenLifetimeInMS, ChronoUnit.MILLIS)
        val accessToken = generateAccessToken(userId, scopes, accessTokenExpiresAt)
        val refreshTokenExpiresAt: LocalDateTime?
        val refreshToken: String?

        if (isRefreshTokenSupported) {
            refreshToken = generateRefreshToken()
            refreshTokenExpiresAt = LocalDateTime.now().plus(refreshTokenLifetimeInMS, ChronoUnit.MILLIS)
        } else {
            refreshToken = null
            refreshTokenExpiresAt = null
        }
        return JwtTokenPair(accessToken, accessTokenExpiresAt, refreshToken, refreshTokenExpiresAt)
    }

    private fun generateAccessToken(userId: UUID, scopes: String, accessTokenExpiresAt: LocalDateTime): String {
        val accessToken = Jwts.builder()
            .subject(userId.toString())
            .audience().add(clientId)
            .and()
            .claim("scope", scopes)
            .expiration(Timestamp.valueOf(accessTokenExpiresAt))
            .let { jwtSigner.sign(it) }
        return accessToken
    }

    private fun generateRefreshToken(): String {
        val randomBytes = ByteArray(156)
        secureRandom.nextBytes(randomBytes)
        return base64Encoder.encodeToString(randomBytes)
    }

    private fun validateScope(rawScope: String) {
        rawScope.split(" ").map { str -> Scope.entries.find { it.stringValue == str } }
            .forEach { if (scopes.contains(it).not()) throw IllegalStateException() }
    }
}
