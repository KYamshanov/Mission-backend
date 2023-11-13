package ru.kyamshanov.mission.point.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tasks_order")
data class TaskOrderEntity(
    @Id
    @Column("id")
    val id: String,
    @Column("next")
    val next: String?,
    @Column("updated_at")
    val updatedAt: LocalDateTime,
)