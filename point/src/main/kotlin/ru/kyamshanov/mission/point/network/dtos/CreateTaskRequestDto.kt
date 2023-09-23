package ru.kyamshanov.mission.point.network.dtos

import ru.kyamshanov.mission.point.database.entities.TaskPriority

data class CreateTaskRequestDto(
    val title: String,
    val description: String,
    val priority: TaskPriority? = null,
)