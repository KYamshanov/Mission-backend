package ru.kyamshanov.mission.identification

import java.util.*

sealed interface IdentificationService {

    suspend fun identify(accessToken: String): UUID

    suspend fun revoke(accessToken: String)
}

interface GithubIdentificationService : IdentificationService