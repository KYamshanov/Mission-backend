package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.point.domain.models.TaskEntity

interface TaskCrudRepository : CoroutineCrudRepository<TaskEntity, String> {

    fun getAllByOwner(ownerId: String): Flow<TaskEntity>

    suspend fun deleteByIdAndOwner(id: String, owner: String)
}