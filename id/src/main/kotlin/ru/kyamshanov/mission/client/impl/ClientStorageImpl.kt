package ru.kyamshanov.mission.client.impl

import io.ktor.util.logging.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.ClientStorage
import ru.kyamshanov.mission.client.issuer.JwtSigner
import ru.kyamshanov.mission.client.issuer.TokenIssuer
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.ResponseType
import ru.kyamshanov.mission.client.models.Scope
import ru.kyamshanov.mission.identification.IdentificationServiceFactory
import ru.kyamshanov.mission.security.SimpleCipher
import ru.kyamshanov.mission.tables.ClientServiceTable
import ru.kyamshanov.mission.tables.ClientsTable
import java.time.LocalDateTime

class ClientStorageImpl(
    private val identificationServiceFactory: IdentificationServiceFactory,
    private val logger: Logger,
    private val tokenCipher: SimpleCipher,
    private val jwtSigner: JwtSigner,
    private val tokenIssuer: TokenIssuer,
    private val authorizationRepository: AuthorizationRepository,
) : ClientStorage {
    override fun getAllClients(): List<Client> = transaction {
        ClientsTable.selectAll()
            .filter {
                val enabled = it[ClientsTable.enabled]
                val id = ClientsTable.id
                logger.info("Initializing client $id. Enabled status: $enabled ")
                enabled
            }
            .filter {
                val isExpired = it[ClientsTable.expiresAt] < LocalDateTime.now()
                if (isExpired) logger.warn("Client life time expired")
                !isExpired
            }
            .map {
                val clientId = it[ClientsTable.id].value
                ClientImpl(
                    clientId = clientId,
                    authorizationGrantTypes = it[ClientsTable.grand_types].split(" ")
                        .map { str -> AuthorizationGrantTypes.entries.first { it.stringValue == str } },
                    authorizationResponseTypes = it[ClientsTable.response_types].split(" ")
                        .map { str -> ResponseType.entries.first { it.stringValue == str } },
                    scopes = it[ClientsTable.scopes].split(" ")
                        .map { str -> Scope.entries.first { it.stringValue == str } },
                    socialServices = ClientServiceTable.select { ClientServiceTable.clientId eq clientId }
                        .map { it[ClientServiceTable.socialServiceId] },
                    redirectUrl = it[ClientsTable.redirectUrl],
                    identificationServiceFactory = identificationServiceFactory,
                    accessTokenLifetimeInMS = it[ClientsTable.metadata].accessTokenLifetimeInMS,
                    refreshTokenLifetimeInMS = it[ClientsTable.metadata].refreshTokenLifetimeInMS,
                    authenticationCodeLifeTimeInMs = it[ClientsTable.metadata].authenticationCodeLifeTimeInMs,
                    rawClientAuthenticationMethod = it[ClientsTable.clientAuthenticationMethods],
                    clientSecret = it[ClientsTable.metadata].secret,
                    tokenCipher = tokenCipher,
                    jwtSigner = jwtSigner,
                    tokenIssuer = tokenIssuer,
                    authorizationRepository = authorizationRepository
                )
            }
    }
}