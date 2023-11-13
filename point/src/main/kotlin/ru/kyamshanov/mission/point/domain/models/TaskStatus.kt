package ru.kyamshanov.mission.point.domain.models

enum class TaskStatus(val weight: Int) {
    CREATED(0), IN_PROCESSING(1), COMPLETED(2)
}