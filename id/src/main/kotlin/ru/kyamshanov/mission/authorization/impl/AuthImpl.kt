package ru.kyamshanov.mission.authorization.impl

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.authorization.Auth
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.delegates.AuthorizationCodeTokenDelegate
import ru.kyamshanov.mission.client.delegates.RefreshTokenDelegate
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.OAuthSessionConfig

class AuthImpl(
    private val clientFactory: ClientFactory,
    private val httpClient: HttpClient,
    private val issuerUrl: String,
) : Auth {

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
                AuthorizationGrantTypes.AUTHORIZATION_CODE -> AuthorizationCodeTokenDelegate(
                    issuerUrl = issuerUrl,
                    clientFactory = clientFactory,
                    formParameters = formParameters,
                )

                AuthorizationGrantTypes.REFRESH_TOKEN -> RefreshTokenDelegate(
                    issuerUrl = issuerUrl,
                    clientFactory = clientFactory,
                    formParameters = formParameters,
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

        println("csrfToken ${csrfToken} saved: ${sessionConfig.csrfToken}")

        if (csrfToken != sessionConfig.csrfToken) throw IllegalStateException("Csrf is not valid")
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
}