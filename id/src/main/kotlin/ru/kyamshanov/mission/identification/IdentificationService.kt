package ru.kyamshanov.mission.identification

import java.util.UUID

sealed interface IdentificationService {

    suspend fun identify(accessToken: String): UUID
}

interface GithubIdentificationService : IdentificationService