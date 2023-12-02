package ru.kyamshanov.mission.point.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.kyamshanov.mission.point.database.entities.AbstractEntity
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import java.time.LocalDateTime

@Table("task_label")
data class TaskLabelEntity(
    @Column("task_id")
    val taskId: String,
    @Column("label_id")
    val labelId: String,
)