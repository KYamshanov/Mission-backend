package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.point.domain.models.LabelEntity


interface LabelCrudRepository : CoroutineCrudRepository<LabelEntity, String> {

    fun findAllByOwner(owner: String): Flow<LabelEntity>


    fun findAllByProjectId(projectId: String): Flow<LabelEntity>
}