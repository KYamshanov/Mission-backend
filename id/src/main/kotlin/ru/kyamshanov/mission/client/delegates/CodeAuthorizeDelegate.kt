package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import java.net.URI

class CodeAuthorizeDelegate(
    private val clientId: String,
    private val scope: String,
    private val clientRedirectUrl: String,
    private val state: String,
    private val csrfToken: String
) : AuthorizeDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val redirectUri = requireNotNull(call.parameters["redirect_uri"]).let { URI.create(it) }
            val callbackUrl = if (clientRedirectUrl.contains(":*/")) {
                clientRedirectUrl.replaceFirst(":*/", ":${redirectUri.port}/")
            } else clientRedirectUrl

            check(callbackUrl == redirectUri.toString()){ "Redirect uri has not been matched with the valid value in database" }

            call.sessions.set(
                OAuthSessionConfig(
                    requireNotNull(call.parameters["code_challenge"]),
                    scope,
                    callbackUrl,
                    clientId,
                    state,
                    csrfToken
                )
            )
            call.respond(FreeMarkerContent("login.ftl", mapOf("csrf_token" to csrfToken)))
        }
}