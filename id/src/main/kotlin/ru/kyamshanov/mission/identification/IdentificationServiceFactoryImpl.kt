package ru.kyamshanov.mission.identification

import ru.kyamshanov.mission.client.models.SocialService

class IdentificationServiceFactoryImpl(
    private val githubIdentification: GithubIdentification
) : IdentificationServiceFactory {
    override fun create(service: SocialService): IdentificationService = when (service) {
        SocialService.GITHUB -> githubIdentification
    }
}