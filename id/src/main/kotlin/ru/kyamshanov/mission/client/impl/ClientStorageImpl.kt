package ru.kyamshanov.mission.client.impl

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.ClientStorage
import ru.kyamshanov.mission.tables.ClientsTable

class ClientStorageImpl : ClientStorage {
    override fun getAllClients(): List<Client> =
        ClientsTable.selectAll().map {
            ClientImpl(clientId = it[ClientsTable.id].value)
        }
}