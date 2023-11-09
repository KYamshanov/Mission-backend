package ru.kyamshanov.mission.identification

import ru.kyamshanov.mission.client.models.SocialService

interface IdentificationServiceFactory {

    fun create(service: SocialService): IdentificationService
}