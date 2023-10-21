package ru.kyamshanov.mission.point.network.dtos

import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import java.time.LocalDateTime

data class GetTaskRsDto(
    val id: String? = null,
    val title: String,
    val description: String,
    val creationTime: LocalDateTime,
    val updateTime: LocalDateTime? = null,
    val completionTime: LocalDateTime? = null,
    val priority: TaskPriority? = null,
    val status: TaskStatus,
    val type: TaskTypeDto,
    val editingRules: EditingRulesDto?
)
