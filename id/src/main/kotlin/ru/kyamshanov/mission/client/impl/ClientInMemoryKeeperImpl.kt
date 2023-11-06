package ru.kyamshanov.mission.client.impl

import ru.kyamshanov.mission.client.Client
import ru.kyamshanov.mission.client.ClientInMemoryKeeper

class ClientInMemoryKeeperImpl : ClientInMemoryKeeper {

    private val clients: MutableMap<String, Client> = mutableMapOf()

    override fun save(vararg client: Client) {
        clients.putAll(client.associateBy { it.clientId })
    }

    override fun getClient(clientId: String): Client? = clients[clientId]
}