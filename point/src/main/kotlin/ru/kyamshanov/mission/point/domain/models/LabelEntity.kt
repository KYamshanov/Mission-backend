package ru.kyamshanov.mission.point.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.kyamshanov.mission.point.database.entities.AbstractEntity
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import java.time.LocalDateTime

@Table("label")
data class LabelEntity(
    @Column("title")
    val title: String,
    @Column("owner")
    val owner: String,
    @Column("project_id")
    val projectId: String,
    @Column("color")
    val color: Long,

    /** Первичный ключ - Идентификатор */
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)