package ru.kyamshanov.mission.dto

import kotlinx.serialization.Serializable

@Serializable
data class GithubUserRsDto(
    val login: String
)