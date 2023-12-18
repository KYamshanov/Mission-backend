package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.ClientAuthorizationDelegate

class ClientAuthorizationDelegateImpl(

) : ClientAuthorizationDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) {
        pipeline.run {
            call.request.headers
        }
    }
}