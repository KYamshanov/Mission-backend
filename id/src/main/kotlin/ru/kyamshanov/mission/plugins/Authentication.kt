package ru.kyamshanov.mission.plugins

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.authentication(httpClient: HttpClient) = install(Authentication) {
    oauth("auth-oauth-github") {
        urlProvider = { "http://127.0.0.1:6543/github/authorized" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "github",
                authorizeUrl = "https://github.com/login/oauth/authorize",
                accessTokenUrl = "https://github.com/login/oauth/access_token",
                requestMethod = HttpMethod.Get,
                clientId = this@authentication.environment.config.property("oauth.github.clientId").getString(),
                clientSecret = this@authentication.environment.config.property("oauth.github.clientSecret").getString(),
                defaultScopes = listOf("read:user"),
                onStateCreated = { call, state ->
                    //redirects[state] = call.request.queryParameters["redirectUrl"]!!
                }
            )
        }
        client = httpClient
    }
}