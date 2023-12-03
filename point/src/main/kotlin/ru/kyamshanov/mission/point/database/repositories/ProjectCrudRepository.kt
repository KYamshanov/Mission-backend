package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.ProjectEntity
import ru.kyamshanov.mission.point.domain.models.TaskEntity
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.domain.models.TaskType


interface ProjectCrudRepository : CoroutineCrudRepository<ProjectEntity, String> {

    fun getAllByOwner(ownerId: String): Flow<ProjectEntity>

    suspend fun findByIdAndOwner(id: String, ownerId: String): ProjectEntity?

    fun findByTitleContainingAndOwner(title: String, owner: String): Flow<ProjectEntity>
}