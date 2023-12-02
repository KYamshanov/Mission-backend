package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.point.domain.models.LabelEntity
import ru.kyamshanov.mission.point.domain.models.TaskLabelEntity


interface TaskLabelCrudRepository : CoroutineCrudRepository<TaskLabelEntity, String> {


    fun findAllByTaskId(taskId: String): Flow<TaskLabelEntity>

    suspend fun deleteByTaskIdAndLabelId(taskId: String, labelId: String)
}