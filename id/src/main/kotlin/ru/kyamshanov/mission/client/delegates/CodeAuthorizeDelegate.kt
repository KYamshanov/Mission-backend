package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.AuthorizeDelegate
import ru.kyamshanov.mission.dto.OAuthSessionConfig

class CodeAuthorizeDelegate(
    private val clientId: String,
    private val scope: String,
    private val redirectUrl: String,
) : AuthorizeDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            call.sessions.set(
                OAuthSessionConfig(
                    requireNotNull(call.parameters["code_challenge"]),
                    scope,
                    redirectUrl,
                    clientId,
                )
            )
            call.respondTemplate("login.ftl")
        }
}