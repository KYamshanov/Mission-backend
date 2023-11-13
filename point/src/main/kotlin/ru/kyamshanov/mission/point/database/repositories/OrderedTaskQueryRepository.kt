package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toCollection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.point.domain.models.TaskOrderEntity
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime


@Repository
class OrderedTaskQueryRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter,
    private val taskOrderCrudRepository: TaskOrderCrudRepository
) {

    fun selectAll(ownerId: String): Flow<TaskOrderEntity> {
        val sqlQuery = """
            SELECT tasks.id as id, next FROM tasks JOIN tasks_order on tasks.id = tasks_order.id WHERE tasks.owner = '$ownerId' ORDER BY updated_at
        """.trimIndent()

        return r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
            .flow()
    }

    @Transactional
    suspend fun orderTask(taskId: String, newPlaceBefore: String?) {
        val time = Timestamp.from(Instant.now()).toString()
        val sqlQuery = """
           INSERT INTO tasks_order VALUES ('$taskId','$newPlaceBefore', '$time') ON CONFLICT (id) DO UPDATE SET next = '$newPlaceBefore', updated_at = '$time' RETURNING *;
        """.trimIndent()

        r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
            .flow()
            .toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Required only one chnages" } }
    }
}
