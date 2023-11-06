package ru.kyamshanov.mission.client

interface ClientFactory {


    fun create(clientId: String): Result<Client>
}