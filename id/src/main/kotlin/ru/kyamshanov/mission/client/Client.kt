package ru.kyamshanov.mission.client

import ru.kyamshanov.mission.client.models.SocialService
import java.util.UUID
import kotlin.jvm.Throws

interface Client {

    val clientId: String

    fun authorize(responseType: String, scope: String): Result<AuthorizeDelegate>

    fun token(grantType: String, issuerUrl: String): Result<TokenDelegate>

    fun authorizedBy(service: SocialService): Result<AuthorizedDelegate>

}