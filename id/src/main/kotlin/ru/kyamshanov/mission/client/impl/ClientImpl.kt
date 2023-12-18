package ru.kyamshanov.mission.client.impl

import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.client.AuthorizedDelegate
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.delegates.AuthorizedBySocialServiceDelegate
import ru.kyamshanov.mission.client.delegates.CodeAuthorizeDelegate
import ru.kyamshanov.mission.client.models.*
import ru.kyamshanov.mission.identification.IdentificationServiceFactory
import ru.kyamshanov.mission.plugins.generateNewToken

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
    private val rawClientAuthenticationMethod: String?
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
                ResponseType.CODE -> CodeAuthorizeDelegate(clientId, scope, redirectUrl, state, generateNewToken())
                ResponseType.TOKEN -> TODO("Authorization by token is not supported now")
                null -> throw IllegalStateException("Authorization response types is invalid")
            }
        }

    override fun authorizedBy(service: SocialService): Result<AuthorizedDelegate> = runCatching {
        AuthorizedBySocialServiceDelegate(
            clientId = clientId,
            socialService = service,
            authenticationCodeLifetimeInMs = authenticationCodeLifeTimeInMs
        )
    }

    private fun validateScope(rawScope: String) {
        rawScope.split(" ").map { str -> Scope.entries.find { it.stringValue == str } }
            .forEach { if (scopes.contains(it).not()) throw IllegalStateException() }
    }
}