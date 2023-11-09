package ru.kyamshanov.mission.client.impl

import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.client.AuthorizedDelegate
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.client.delegates.AuthorizationCodeTokenDelegate
import ru.kyamshanov.mission.client.delegates.AuthorizedBySocialServiceDelegate
import ru.kyamshanov.mission.client.delegates.CodeAuthorizeDelegate
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.ResponseType
import ru.kyamshanov.mission.client.models.Scope
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.identification.IdentificationServiceFactory

data class ClientImpl(
    override val clientId: String,
    private val authorizationGrantTypes: List<AuthorizationGrantTypes>,
    private val authorizationResponseTypes: List<ResponseType>,
    private val scopes: List<Scope>,
    private val redirectUrl: String,
    private val socialServices: List<SocialService>,
    private val identificationServiceFactory: IdentificationServiceFactory
) : Client {

    override val identificationServices = socialServices.associateWith { identificationServiceFactory.create(it) }

    override fun authorize(responseType: String, scope: String): Result<AuthorizeDelegate> = runCatching {
        validateScope(scope)

        when (authorizationResponseTypes.find { it.stringValue == responseType }) {
            ResponseType.CODE -> CodeAuthorizeDelegate(clientId, scope, redirectUrl)
            ResponseType.TOKEN -> TODO()
            null -> TODO()
        }
    }

    override fun authorizedBy(service: SocialService): Result<AuthorizedDelegate> = runCatching {
        AuthorizedBySocialServiceDelegate(
            clientId, service
        )
    }

    private fun validateScope(rawScope: String) {
        rawScope.split(" ").map { str -> Scope.entries.find { it.stringValue == str } }
            .forEach { if (scopes.contains(it).not()) throw IllegalStateException() }
    }
}