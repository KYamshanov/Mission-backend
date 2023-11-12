package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.point.domain.models.TaskOrderEntity


@Repository
class OrderedTaskCrudRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter
) {

    fun selectAll(ownerId: String): Flow<TaskOrderEntity> {
        val sqlQuery = """
            SELECT tasks.id as id, next FROM tasks JOIN tasks_order on tasks.id = tasks_order.id WHERE tasks.owner = '$ownerId'
        """.trimIndent()

        return r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
            .flow()
    }

    fun orderTask(taskId: String, placeBefore: String?): Flow<TaskOrderEntity> {
        val sqlQuery = """
            INSERT INTO tasks_order VALUES ('$taskId','$placeBefore') ON CONFLICT (id) DO UPDATE SET next = '$placeBefore' RETURNING *;
        """.trimIndent()

        return r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
            .flow()
    }
}
