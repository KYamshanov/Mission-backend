package ru.kyamshanov.mission.client.impl

import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.ClientInMemoryKeeper
import ru.kyamshanov.mission.client.ClientStorage
import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes

class ClientFactoryImpl(
    clientStorage: ClientStorage,
    private val clientInMemoryKeeper: ClientInMemoryKeeper
) : ClientFactory {

    init {
        clientInMemoryKeeper.save(*clientStorage.getAllClients().toTypedArray())
    }

    override fun create(clientId: String): Result<Client> = runCatching {
        clientInMemoryKeeper.getClient(clientId) ?: throw IllegalStateException("Client is not loaded")
    }
}