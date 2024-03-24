package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskEntity
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.domain.models.TaskType


interface TaskCrudRepository : CoroutineCrudRepository<TaskEntity, String> {

    fun getAllByOwnerAndAndDeleted(ownerId: String, deleted: Boolean): Flow<TaskEntity>

    suspend fun getFirstByOwnerAndId(ownerId: String, id: String): TaskEntity?


    @Modifying
    @Query("update tasks set deleted = true where id = :taskId and owner = :ownerId RETURNING *;")
    suspend fun deleteByIdAndOwner(id: String, owner: String)

    @Modifying
    @Query("update tasks set type = :type where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskType(taskId: String, ownerId: String, type: TaskType?): Flow<TaskEntity>

    @Modifying
    @Query("update tasks set status = :status where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskStatus(taskId: String, ownerId: String, status: TaskStatus): Flow<TaskEntity>

    fun findByTitleContainingAndOwner(title: String, owner: String): Flow<TaskEntity>

    @Modifying
    @Query("update tasks set priority = :priority where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskPriority(taskId: String, ownerId: String, priority: TaskPriority?): Flow<TaskEntity>

    @Modifying
    @Query("update tasks set title = :title where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskTitle(ownerId: String, taskId: String, title: String): Flow<TaskEntity>

    @Modifying
    @Query("update tasks set description = :description where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskDescription(ownerId: String, taskId: String, description: String): Flow<TaskEntity>

    @Modifying
    @Query("update tasks set description = :description, title = :title where id = :taskId and owner = :ownerId RETURNING *;")
    fun updateTaskTitleDescription(
        ownerId: String,
        taskId: String,
        title: String,
        description: String
    ): Flow<TaskEntity>
}