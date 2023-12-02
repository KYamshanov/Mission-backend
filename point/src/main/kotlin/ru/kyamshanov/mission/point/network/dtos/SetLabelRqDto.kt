package ru.kyamshanov.mission.point.network.dtos

data class SetLabelRqDto(
    val taskId: String,
    val labels: List<String>
)