package ru.kyamshanov.mission.plugins

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import ru.kyamshanov.mission.authorization.AuthInterceptor
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.utils.secret

fun Application.authentication(httpClient: HttpClient, authInterceptor: AuthInterceptor) = install(Authentication) {
    oauth("auth-oauth-github") {
        urlProvider = { this@authentication.environment.config.secret("oauth.github.urlProvider") }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "github",
                authorizeUrl = "https://github.com/login/oauth/authorize",
                accessTokenUrl = "https://github.com/login/oauth/access_token",
                requestMethod = HttpMethod.Get,
                clientId = this@authentication.environment.config.secret("oauth.github.clientId"),
                clientSecret = this@authentication.environment.config.secret("oauth.github.clientSecret"),
                defaultScopes = listOf("read:user"),
                onStateCreated = { call, _ -> authInterceptor.loginBy(SocialService.GITHUB, call) }
            )
        }
        client = httpClient
    }
}