package ru.kyamshanov.mission.client

import ru.kyamshanov.mission.client.models.AuthorizationGrantTypes

interface ClientFactory {


    fun create(clientId: String): Result<Client>
}