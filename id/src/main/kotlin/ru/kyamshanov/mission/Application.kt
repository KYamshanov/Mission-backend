package ru.kyamshanov.mission

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import ru.kyamshanov.mission.plugins.configureRouting

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val applicationHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.module(httpClient: HttpClient = applicationHttpClient) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(Authentication) {
        oauth("auth-oauth-github") {
            urlProvider = { "http://127.0.0.1:8080/desktop/authorized" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://github.com/login/oauth/authorize",
                    accessTokenUrl = "https://github.com/login/oauth/access_token",
                    requestMethod = HttpMethod.Get,
                    clientId = "SECRET",
                    clientSecret = "SECRET",
                    defaultScopes = listOf("read:user"),
                    onStateCreated = { call, state ->
                        //redirects[state] = call.request.queryParameters["redirectUrl"]!!
                    }
                )
            }
            client = httpClient
        }
    }
    configureRouting()
}
