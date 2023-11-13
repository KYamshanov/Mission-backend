package ru.kyamshanov.mission.point.database.entities

enum class TaskPriority(val weight: Int) {
    PRIMARY(0), LOW(2),
}