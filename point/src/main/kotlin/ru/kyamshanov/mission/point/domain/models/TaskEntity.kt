package ru.kyamshanov.mission.point.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.kyamshanov.mission.point.database.entities.AbstractEntity
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import java.time.LocalDateTime

@Table("tasks")
data class TaskEntity(
    @Column("title")
    val title: String,
    @Column("description")
    val description: String,
    @Column("creation_time")
    val creationTime: LocalDateTime,
    @Column("update_time")
    val updateTime: LocalDateTime? = null,
    @Column("completion_time")
    val completionTime: LocalDateTime? = null,
    @Column("priority")
    val priority: TaskPriority? = null,
    @Column("status")
    val status: TaskStatus = TaskStatus.CREATED,
    @Column("owner")
    val owner: String,
    @Column("type")
    val type: TaskType? = null,

    /** Первичный ключ - Идентификатор */
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)