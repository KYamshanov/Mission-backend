package ru.kyamshanov.mission.point.database.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toCollection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.point.domain.models.LabelEntity
import ru.kyamshanov.mission.point.domain.models.TaskOrderEntity
import ru.kyamshanov.mission.point.network.dtos.RequestOrderTaskDto
import java.sql.Timestamp
import java.time.Instant
import java.time.ZonedDateTime


@Repository
class LabelQueryRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter,
) {

    fun selectByTaskId(taskId: String): Flow<LabelEntity> {
        val sqlQuery = """
            SELECT (t.*)
            FROM task_label
            RIGHT JOIN public.label t on t.id = task_label.label_id
            WHERE task_label.task_id = '$taskId'
        """.trimIndent()

        return r2dbcEntityTemplate.databaseClient.sql(sqlQuery)
            .map { t, u -> converter.read(LabelEntity::class.java, t, u) }
            .flow()
    }
}
