package ru.kyamshanov.mission.point.domain.models

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tasks_order")
data class TaskOrderEntity(
    @Column("id")
    val id: String,
    @Column("next")
    val next: String? = null,
)