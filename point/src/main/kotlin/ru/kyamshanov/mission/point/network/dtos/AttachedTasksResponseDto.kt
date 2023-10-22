package ru.kyamshanov.mission.point.network.dtos

import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import java.time.LocalDateTime

data class AttachedTasksResponseDto(
    val items: List<TaskSlim>
) {

    data class TaskSlim(
        val id: String? = null,
        val title: String,
        val creationTime: LocalDateTime,
        val completionTime: LocalDateTime? = null,
        val priority: TaskPriority? = null,
        val status: TaskStatus = TaskStatus.CREATED,
        val type: TaskTypeDto
    )
}