package ru.kyamshanov.mission.client.delegates

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.ClientAuthorizationDelegate
import java.util.Base64

class BasicClientAuthorizationDelegate(
    private val clientId: String,
    private val clientSecret: String
) : ClientAuthorizationDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) {
        val authorizationHeader = pipeline.call.request.header(HttpHeaders.Authorization)
        checkNotNull(authorizationHeader) { "Authorization header cannot be null" }
        check(authorizationHeader.startsWith(BASIC_PREFIX)) { "Authorization header have to start with `$BASIC_PREFIX` prefix" }
        val clientIdSecretPair = Base64.getDecoder().decode(authorizationHeader.removePrefix(BASIC_PREFIX))
            .decodeToString()
            .split(":", limit = 2)
            .let { Pair(it[0], it[1]) }
        check(clientId == clientIdSecretPair.first) { "ClientId not matched with required value" }
        check(clientSecret == clientIdSecretPair.second) { "Client secret not matched with required value" }
    }

    companion object {


        private const val BASIC_PREFIX = "Basic "
    }
}