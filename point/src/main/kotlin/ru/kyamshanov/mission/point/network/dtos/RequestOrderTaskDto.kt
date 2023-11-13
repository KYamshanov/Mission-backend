package ru.kyamshanov.mission.point.network.dtos

data class RequestOrderTaskDto(
    val taskId: String,
    val oldPlaceBefore: String?,
    val newPlaceBefore: String?
)