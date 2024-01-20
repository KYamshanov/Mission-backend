package ru.kyamshanov.mission

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.doublereceive.*
import kotlinx.serialization.json.Json
import ru.kyamshanov.mission.authorization.impl.AuthInterceptorImpl
import ru.kyamshanov.mission.authorization.impl.AuthorizationDatabaseRepository
import ru.kyamshanov.mission.authorization.impl.UserDatabaseRepository
import ru.kyamshanov.mission.client.impl.ClientFactoryImpl
import ru.kyamshanov.mission.client.impl.ClientInMemoryKeeperImpl
import ru.kyamshanov.mission.client.impl.ClientStorageImpl
import ru.kyamshanov.mission.client.issuer.SecureRandomTokenIssuer
import ru.kyamshanov.mission.client.issuer.SignedJwtSigner
import ru.kyamshanov.mission.identification.GithubIdentification
import ru.kyamshanov.mission.identification.IdentificationServiceFactoryImpl
import ru.kyamshanov.mission.plugins.*
import ru.kyamshanov.mission.security.SymmetricCipher
import ru.kyamshanov.mission.utils.secret
import java.io.FileInputStream
import java.security.KeyStore


/**
 * Main function for initialize application
 */
fun main(args: Array<String>) = EngineMain.main(args)


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

    val issuer = this.environment.config.secret("oauth.issuer")
    val tokenKeyAlias = this.environment.config.secret("oauth.security.cipher.keyAlias")
    val tokenKeyPassword = this.environment.config.secret("oauth.security.cipher.keyPassword")
    val tokenKeyStore = this.environment.config.secret("oauth.security.cipher.keyStore")
    val tokenKeyStorePassword = this.environment.config.secret("oauth.security.cipher.keyStorePassword")

    val tokenCipher = SymmetricCipher(
        KeyStore.getInstance(KeyStore.getDefaultType())
            .apply { load(FileInputStream(tokenKeyStore), tokenKeyStorePassword.toCharArray()) },
        tokenKeyAlias, tokenKeyPassword.toCharArray()
    )

    val jwtSigner = SignedJwtSigner(issuer)
    val tokenIssuer = SecureRandomTokenIssuer()

    val authorizationRepository = AuthorizationDatabaseRepository()
    val userRepository = UserDatabaseRepository()

    val clientId = this.environment.config.secret("oauth.github.clientId")
    val clientSecret = this.environment.config.secret("oauth.github.clientSecret")

    val identificationServiceFactory = IdentificationServiceFactoryImpl(
        githubIdentification = GithubIdentification(
            httpClient = httpClient,
            userRepository = userRepository,
            clientId, clientSecret
        )
    )
    val auth = AuthInterceptorImpl(
        clientFactory = ClientFactoryImpl(
            clientStorage = ClientStorageImpl(
                identificationServiceFactory = identificationServiceFactory,
                logger = log,
                tokenCipher = tokenCipher,
                jwtSigner = jwtSigner,
                tokenIssuer = tokenIssuer,
                authorizationRepository = authorizationRepository
            ), clientInMemoryKeeper = ClientInMemoryKeeperImpl()
        ),
        httpClient = httpClient,
        tokenCipher = tokenCipher,
        authorizationRepository = authorizationRepository,
        userRepository = userRepository,
        identificationServiceFactory = identificationServiceFactory
    )
    authentication(httpClient, auth)
    configureRouting(httpClient, auth)
    configureResourcesRouting()
    exceptionHandler()
}