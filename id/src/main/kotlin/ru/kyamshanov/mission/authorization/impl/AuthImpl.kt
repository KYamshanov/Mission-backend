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
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import kotlin.requireNotNull

class AuthImpl(
    private val clientFactory: ClientFactory,
    private val httpClient: HttpClient,
    private val issuerUrl: String,
) : Auth {

    override suspend fun authorize(pipeline: PipelineContext<Unit, ApplicationCall>) {
        pipeline.runCatching {
            val clientId = requireNotNull(call.parameters["client_id"])
            val responseType = requireNotNull(call.parameters["response_type"])
            val scope = requireNotNull(call.parameters["scope"])

            val client = clientFactory.create(clientId).getOrThrow()
            client.authorize(responseType, scope).getOrThrow().execute(this, httpClient)
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
                    issuerUrl,
                    clientFactory,
                    formParameters
                )

                AuthorizationGrantTypes.REFRESH_TOKEN -> TODO()
            }

            d.execute(this, httpClient)
        }.onFailure {
            it.printStackTrace()
            pipeline.call.respond(HttpStatusCode.InternalServerError)
        }
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