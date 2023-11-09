package ru.kyamshanov.mission.client

import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.identification.IdentificationService
import java.util.UUID
import kotlin.jvm.Throws

interface Client {

    val clientId: String

    val identificationServices: Map<SocialService, IdentificationService>

    fun authorize(responseType: String, scope: String): Result<AuthorizeDelegate>

    fun authorizedBy(service: SocialService): Result<AuthorizedDelegate>

}