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
import kotlinx.serialization.json.Json
import ru.kyamshanov.mission.plugins.*

/**
 * Main function for initialize application
 */
fun main(args: Array<String>): Unit = EngineMain.main(args)


/**
 * Http client for make requests
 */
private val applicationHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

fun Application.module(httpClient: HttpClient = applicationHttpClient) {
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json()
    }
    dbConnect()
    configureSession()
    freeMarker()
    authentication(httpClient)
    configureRouting(httpClient)
}
