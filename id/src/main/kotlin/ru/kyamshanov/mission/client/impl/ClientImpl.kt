package ru.kyamshanov.mission.client.impl

import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.client.AuthorizedDelegate
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.client.delegates.AuthorizationCodeTokenDelegate
import ru.kyamshanov.mission.client.delegates.CodeAuthorizeDelegate
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.ResponseType
import ru.kyamshanov.mission.client.models.Scope
import ru.kyamshanov.mission.client.models.SocialService

data class ClientImpl(
    override val clientId: String,
    private val authorizationGrantTypes: List<AuthorizationGrantTypes>,
    private val authorizationResponseTypes: List<ResponseType>,
    private val scopes: List<Scope>,
    private val redirectUrl: String
) : Client {


    override fun authorize(responseType: String, scope: String): Result<AuthorizeDelegate> = runCatching {
        validateScope(scope)

        when (authorizationResponseTypes.find { it.stringValue == responseType }) {
            ResponseType.CODE -> CodeAuthorizeDelegate(clientId, scope, redirectUrl)
            ResponseType.TOKEN -> TODO()
            null -> TODO()
        }
    }

    override fun token(grantType: String, issuerUrl: String): Result<TokenDelegate> = runCatching {
        when (authorizationGrantTypes.find { it.stringValue == grantType }) {
            AuthorizationGrantTypes.AUTHORIZATION_CODE -> AuthorizationCodeTokenDelegate(issuerUrl)
            AuthorizationGrantTypes.REFRESH_TOKEN -> TODO()
            null -> TODO()
        }
    }

    override fun authorizedBy(service: SocialService): Result<AuthorizedDelegate> {
        TODO("Not yet implemented")
    }

    private fun validateScope(rawScope: String) {
        rawScope.split(" ").map { Scope.valueOf(it) }
            .forEach { if (scopes.contains(it).not()) throw IllegalStateException() }
    }
}