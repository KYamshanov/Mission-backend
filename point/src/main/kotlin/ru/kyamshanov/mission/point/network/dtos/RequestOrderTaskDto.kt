package ru.kyamshanov.mission.point.network.dtos

data class RequestOrderTaskDto(
    val taskId: String,
    val putBefore: String?,
    val newTaskBefore: String?,
    val oldBeforeTask: String?,
    val oldAfterTask: String?,
)