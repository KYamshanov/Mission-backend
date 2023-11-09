package ru.kyamshanov.mission.client.impl

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.ClientStorage
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes
import ru.kyamshanov.mission.client.models.ResponseType
import ru.kyamshanov.mission.client.models.Scope
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.identification.IdentificationServiceFactory
import ru.kyamshanov.mission.tables.ClientServiceTable
import ru.kyamshanov.mission.tables.ClientsTable

class ClientStorageImpl(
    private val identificationServiceFactory: IdentificationServiceFactory
) : ClientStorage {
    override fun getAllClients(): List<Client> = transaction {
        ClientsTable.selectAll().map {
            val clientId = it[ClientsTable.id].value.toString()
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
                identificationServiceFactory = identificationServiceFactory
            )
        }
    }
}