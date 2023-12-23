package ru.kyamshanov.mission.authorization.impl

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.authorization.AuthInterceptor
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.authorization.UserRepository
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.delegates.RefreshTokenDelegate
import ru.kyamshanov.mission.client.delegates.TokenByAuthorizationCodeDelegate
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import ru.kyamshanov.mission.exception.verify
import ru.kyamshanov.mission.identification.IdentificationServiceFactory
import ru.kyamshanov.mission.security.SimpleCipher

class AuthInterceptorImpl(
    private val clientFactory: ClientFactory,
    private val httpClient: HttpClient,
    private val tokenCipher: SimpleCipher,
    private val authorizationRepository: AuthorizationRepository,
    private val userRepository: UserRepository,
    private val identificationServiceFactory: IdentificationServiceFactory,
) : AuthInterceptor {

    override suspend fun authorize(pipeline: PipelineContext<Unit, ApplicationCall>) {
        pipeline.runCatching {
            val clientId = requireNotNull(call.parameters["client_id"]) { "Property client_id required" }
            val responseType = requireNotNull(call.parameters["response_type"]) { "Property response_type required" }
            val scope = requireNotNull(call.parameters["scope"]) { "Property scope required" }
            val state = requireNotNull(call.parameters["state"]) { "Property state required" }

            val client = clientFactory.create(clientId).getOrThrow()
            client.authorize(responseType, scope, state).getOrThrow().execute(this, httpClient)
        }.onFailure {
            it.printStackTrace()
            pipeline.call.respond(HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun token(pipeline: PipelineContext<Unit, ApplicationCall>) {
        pipeline.runCatching {
            val formParameters = call.receiveParameters()
            val grantType =
                requireNotNull(formParameters["grant_type"].toString()).let { str -> AuthorizationGrantTypes.entries.first { it.stringValue == str } }

            val d = when (grantType) {
                AuthorizationGrantTypes.AUTHORIZATION_CODE -> TokenByAuthorizationCodeDelegate(
                    clientFactory = clientFactory,
                    formParameters = formParameters,
                    tokenCipher = tokenCipher,
                    authorizationRepository = authorizationRepository
                )

                AuthorizationGrantTypes.REFRESH_TOKEN -> RefreshTokenDelegate(
                    clientFactory = clientFactory,
                    formParameters = formParameters,
                    authorizationRepository = authorizationRepository,
                    userRepository = userRepository,
                    simpleCipher = tokenCipher,
                )
            }

            d.execute(this, httpClient)
        }.onFailure {
            it.printStackTrace()
            pipeline.call.respond(HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun loginBy(service: SocialService, call: ApplicationCall) {
        val formParameters = call.receiveParameters()
        val csrfToken = requireNotNull(formParameters["_csrf"].toString())
        val sessionConfig = requireNotNull(call.sessions.get<OAuthSessionConfig>())
        verify(csrfToken == sessionConfig.csrfToken) { HttpStatusCode.Forbidden to "Csrf is not valid" }
    }

    override suspend fun authorizedBy(service: SocialService, pipeline: PipelineContext<Unit, ApplicationCall>) {
        pipeline.runCatching {
            val clientId = requireNotNull(call.sessions.get<OAuthSessionConfig>()).clientId
            val client = clientFactory.create(clientId).getOrThrow()
            client.authorizedBy(service).getOrThrow().execute(this, httpClient)
        }.onFailure {
            it.printStackTrace()
            pipeline.call.respond(HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun logout(pipeline: PipelineContext<Unit, ApplicationCall>) {
        val authorizationHeader = pipeline.call.request.header(HttpHeaders.Authorization)
        checkNotNull(authorizationHeader)
        check(authorizationHeader.startsWith("Bearer "))
        val accessToken = authorizationHeader.removePrefix("Bearer ")
        val authorization = authorizationRepository.runCatching { findFirstByAccessToken(accessToken) }.getOrNull()
        checkNotNull(authorization) { "Authorization has not been found with authorization $authorizationHeader" }
        verify(authorization.enabled) { HttpStatusCode.Unauthorized to "Authorization has been disabled" }
        checkNotNull(authorization.socialService) { "Authorization has no social service" }
        checkNotNull(authorization.metadata) { "Authorization metadata did not establish" }
        checkNotNull(authorization.refreshToken) { "Refresh token has not establish" }
        identificationServiceFactory.create(authorization.socialService)
            .revoke(authorization.metadata.encryptedToken.let { tokenCipher.decrypt(it) })
        authorizationRepository.disableAuthorizationByRefreshToken(authorization.refreshToken)
        pipeline.call.respond(HttpStatusCode.NoContent)
    }
}