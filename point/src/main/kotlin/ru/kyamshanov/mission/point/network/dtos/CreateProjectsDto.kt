package ru.kyamshanov.mission.point.network.dtos

data class CreateProjectsRqDto(
    val title: String,
    val description: String?,
)

data class CreateProjectsRsDto(
    val id: String
)
