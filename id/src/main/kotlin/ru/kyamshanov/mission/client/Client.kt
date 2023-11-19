package ru.kyamshanov.mission.client

import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.identification.IdentificationService

interface Client {

    val clientId: String

    val identificationServices: Map<SocialService, IdentificationService>

    fun authorize(responseType: String, scope: String, state: String): Result<AuthorizeDelegate>

    fun authorizedBy(service: SocialService): Result<AuthorizedDelegate>

}