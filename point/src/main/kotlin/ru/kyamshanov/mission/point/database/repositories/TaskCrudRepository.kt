package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.point.domain.models.TaskEntity
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.domain.models.TaskType


interface TaskCrudRepository : CoroutineCrudRepository<TaskEntity, String> {

    fun getAllByOwner(ownerId: String): Flow<TaskEntity>

    suspend fun deleteByIdAndOwner(id: String, owner: String)

    @Modifying
    @Query("update tasks set type = :type where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskType(taskId: String, ownerId: String, type: TaskType?): Flow<TaskEntity>

    @Modifying
    @Query("update tasks set status = :status where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskStatus(taskId: String, ownerId: String, status: TaskStatus): Flow<TaskEntity>
}