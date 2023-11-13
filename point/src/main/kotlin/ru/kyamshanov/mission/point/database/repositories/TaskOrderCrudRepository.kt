package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.point.domain.models.TaskOrderEntity
import java.time.LocalDateTime


@Repository
interface TaskOrderCrudRepository : CoroutineCrudRepository<TaskOrderEntity, String> {

    @Modifying
    @Query("INSERT INTO tasks_order VALUES (:id,:next, :time) ON CONFLICT (id) DO UPDATE SET next = :next and updated_at = :time RETURNING *;")
    fun insert(id: String, next: String?, time: LocalDateTime): Flow<TaskOrderEntity>

    suspend fun findByNext(next: String): TaskOrderEntity?
}