package ru.kyamshanov.mission.point.network.dtos

import ru.kyamshanov.mission.point.domain.models.TaskType
import ru.kyamshanov.mission.point.network.dtos.TaskTypeDto.*

enum class TaskTypeDto {
    /**
     * today`s
     */
    TODAYS,

    /**
     * week`s
     */
    WEEKS,

    UNKNOWN,
}

fun TaskTypeDto.toDomain(): TaskType? = when (this) {
    TODAYS -> TaskType.TODAYS
    WEEKS -> TaskType.WEEKS
    UNKNOWN -> null
}

fun TaskType?.toDto(): TaskTypeDto = when (this) {
    TaskType.TODAYS -> TODAYS
    TaskType.WEEKS -> WEEKS
    null -> UNKNOWN
}