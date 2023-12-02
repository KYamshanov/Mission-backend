package ru.kyamshanov.mission.point.network.dtos

data class AllProjectsRsDto(
    val items: List<ProjectSlimDto>
) {
    data class ProjectSlimDto(
        val id: String,
        val title: String,
    )
}
