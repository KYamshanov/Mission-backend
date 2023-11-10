package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.dto.OAuthSessionConfig
import java.net.URI

class CodeAuthorizeDelegate(
    private val clientId: String,
    private val scope: String,
    private val clientRedirectUrl: String,
) : AuthorizeDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val redirectUri = requireNotNull(call.parameters["redirect_uri"]).let { URI.create(it) }
            val callbackUrl = if (clientRedirectUrl.contains(":*/")) {
                clientRedirectUrl.replaceFirst(":*/", ":${redirectUri.port}/")
            } else clientRedirectUrl

            assert(callbackUrl == redirectUri.toString())

            call.sessions.set(
                OAuthSessionConfig(
                    requireNotNull(call.parameters["code_challenge"]),
                    scope,
                    callbackUrl,
                    clientId,
                )
            )
            call.respondTemplate("login.ftl")
        }
}