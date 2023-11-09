package ru.kyamshanov.mission.identification

sealed interface IdentificationService {

    suspend fun identify(accessToken: String): String
}

interface GithubIdentificationService : IdentificationService