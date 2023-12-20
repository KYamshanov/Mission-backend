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
import java.io.FileInputStream
import java.security.KeyStore

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
    val tokenKeyAlias = this.environment.config.property("oauth.security.cipher.keyAlias").getString()
    val tokenKeyPassword = this.environment.config.property("oauth.security.cipher.keyPassword").getString()
    val tokenKeyStore = this.environment.config.property("oauth.security.cipher.keyStore").getString()
    val tokenKeyStorePassword = this.environment.config.property("oauth.security.cipher.keyStorePassword").getString()

    val tokenCipher = SymmetricCipher(
        KeyStore.getInstance(KeyStore.getDefaultType())
            .apply { load(FileInputStream(tokenKeyStore), tokenKeyStorePassword.toCharArray()) },
        tokenKeyAlias, tokenKeyPassword.toCharArray()
    )

    val jwtSigner = SignedJwtSigner(issuer)
    val tokenIssuer = SecureRandomTokenIssuer()

    val authorizationRepository = AuthorizationDatabaseRepository()
    val userRepository = UserDatabaseRepository()

    val auth = AuthInterceptorImpl(
        clientFactory = ClientFactoryImpl(
            clientStorage = ClientStorageImpl(
                identificationServiceFactory = IdentificationServiceFactoryImpl(
                    githubIdentification = GithubIdentification(
                        httpClient = httpClient,
                        userRepository = userRepository
                    )
                ),
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
        userRepository = userRepository
    )
    authentication(httpClient, auth)
    configureRouting(httpClient, auth)
}