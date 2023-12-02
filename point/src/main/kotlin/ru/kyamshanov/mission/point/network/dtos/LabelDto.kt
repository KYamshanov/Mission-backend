package ru.kyamshanov.mission.point.network.dtos

data class LabelRsDto(
    val items: List<LabelDto>
)

data class LabelDto(
    val id: String,
    val title: String,
    val color: Long,
)
