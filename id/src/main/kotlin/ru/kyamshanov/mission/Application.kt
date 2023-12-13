package ru.kyamshanov.mission

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.doublereceive.*
import kotlinx.serialization.json.Json
import ru.kyamshanov.mission.authorization.impl.AuthImpl
import ru.kyamshanov.mission.client.impl.ClientFactoryImpl
import ru.kyamshanov.mission.client.impl.ClientInMemoryKeeperImpl
import ru.kyamshanov.mission.client.impl.ClientStorageImpl
import ru.kyamshanov.mission.identification.GithubIdentification
import ru.kyamshanov.mission.identification.IdentificationServiceFactoryImpl
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
    install(DoubleReceive)
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json()
    }
    dbConnect()
    configureSession()
    freeMarker()
    val issuer = this.environment.config.property("oauth.issuer").getString()

    val auth = AuthImpl(
        ClientFactoryImpl(
            ClientStorageImpl(
                IdentificationServiceFactoryImpl(
                    GithubIdentification(httpClient)
                )
            ), ClientInMemoryKeeperImpl()
        ), httpClient, issuer
    )
    authentication(httpClient, auth)
    configureRouting(
        httpClient, auth
    )
}