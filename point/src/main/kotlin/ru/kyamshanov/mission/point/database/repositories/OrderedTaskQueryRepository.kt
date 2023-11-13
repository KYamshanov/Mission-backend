package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toCollection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.point.domain.models.TaskOrderEntity
import ru.kyamshanov.mission.point.network.dtos.RequestOrderTaskDto
import java.sql.Timestamp
import java.time.Instant
import java.time.ZonedDateTime


@Repository
class OrderedTaskQueryRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter,
    private val taskOrderCrudRepository: TaskOrderCrudRepository
) {

    fun selectAll(ownerId: String): Flow<TaskOrderEntity> {
        val sqlQuery = """
            SELECT tasks.id as id, next, updated_at FROM tasks JOIN tasks_order on tasks.id = tasks_order.id WHERE tasks.owner = '$ownerId' ORDER BY updated_at
        """.trimIndent()

        return r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
            .flow()
    }

    @Transactional
    suspend fun orderTask(info: RequestOrderTaskDto) {
        val now = Instant.now()
        val sqlQuery = """
           INSERT INTO tasks_order (id, next,updated_at) 
           VALUES ${
            buildList {
                add("('${info.taskId}', ${info.putBefore?.let { "'$it'" }}, '${Timestamp.from(now)}')")
                if (info.newTaskBefore != null) {
                    add("('${info.newTaskBefore}', '${info.taskId}', '${Timestamp.from(now.plusMillis(1))}')")
                }
                if (info.oldBeforeTask != null) {
                    add(
                        "('${info.oldBeforeTask}', ${info.oldAfterTask?.let { "'$it'" }}, '${
                            Timestamp.from(now.plusMillis(2))
                        }')"
                    )
                }
            }.joinToString(",")
        }
           ON CONFLICT (id) DO UPDATE SET next=EXCLUDED.next, updated_at=EXCLUDED.updated_at RETURNING *;
        """.trimIndent()

        r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
            .flow()
            .toCollection(mutableListOf())
            .also { assert(it.size in 1..3) { "Required only 1..3 chnages but it has ${it.size}" } }

        /*    @Transactional
            suspend fun orderTask(taskId: String, newPlaceBefore: String?) {
                var placeBefore = newPlaceBefore
                if (newPlaceBefore != null) {
                    taskOrderCrudRepository.findById(taskId)?.let { olderOrder ->
                        taskOrderCrudRepository.findAllByNext(taskId).toCollection(mutableListOf()).forEach { tE ->
                            if (tE.id != olderOrder.next && tE.updatedAt.isAfter(olderOrder.updatedAt)) {
                                tE.copy(next = olderOrder.next)
                                    .also { taskOrderCrudRepository.save(it) }
                            }
                        }
                    }
                }

                val time = Timestamp.from(Instant.now()).toString()
                //TODO find algorithm for optimization rows
                val sqlQuery = """
                   INSERT INTO tasks_order VALUES ('$taskId',${placeBefore?.let { "'$it'" }}, '$time') ON CONFLICT (id) DO UPDATE SET next = ${placeBefore?.let { "'$it'" }}, updated_at = '$time' RETURNING *;
                """.trimIndent()

                r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
                    .map { t, u -> converter.read(TaskOrderEntity::class.java, t, u) }
                    .flow()
                    .toCollection(mutableListOf())
                    .also { assert(it.size == 1) { "Required only one chnages" } }
            }*/
    }

    @Transactional
    suspend fun removeOrder(taskId: String) {
        val current = taskOrderCrudRepository.findById(taskId) ?: return
        taskOrderCrudRepository.findAllByNext(taskId).toCollection(mutableListOf())
            .forEach { prev -> prev.copy(next = current.next).also { taskOrderCrudRepository.save(it) } }
        taskOrderCrudRepository.deleteById(taskId)
    }
}
