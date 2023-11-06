package ru.kyamshanov.mission.client

interface ClientInMemoryKeeper {

    fun save(vararg client: Client)

    fun getClient(clientId: String): Client?
}