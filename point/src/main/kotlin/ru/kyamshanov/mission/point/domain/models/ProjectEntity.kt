package ru.kyamshanov.mission.point.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.kyamshanov.mission.point.database.entities.AbstractEntity
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import java.time.LocalDateTime

@Table("projects")
data class ProjectEntity(
    @Column("title")
    val title: String,
    @Column("description")
    val description: String?,
    @Column("owner")
    val owner: String,

    /** Первичный ключ - Идентификатор */
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)