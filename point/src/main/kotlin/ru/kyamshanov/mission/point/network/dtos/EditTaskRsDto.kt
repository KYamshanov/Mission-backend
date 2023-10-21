package ru.kyamshanov.mission.point.network.dtos

import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import java.time.LocalDateTime

data class EditTaskRsDto(
    val id: String,
    val title: String? = null,
    val description: String? = null,
)
