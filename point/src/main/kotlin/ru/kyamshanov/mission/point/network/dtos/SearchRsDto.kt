package ru.kyamshanov.mission.point.network.dtos

import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import java.time.LocalDateTime

data class SearchRsDto(
    val tasks: List<TaskSlim>,
    val projects: List<ProjectSlim>
) {

    data class TaskSlim(
        val id: String,
        val title: String,
        val creationTime: LocalDateTime,
        val completionTime: LocalDateTime? = null,
        val priority: TaskPriority? = null,
        val status: TaskStatus = TaskStatus.CREATED,
        val type: TaskTypeDto,
    )

    data class ProjectSlim(
        val id: String? = null,
        val title: String,
    )
}