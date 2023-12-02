package ru.kyamshanov.mission.point.network

import kotlinx.coroutines.flow.toCollection
import org.springframework.data.util.Pair
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.database.repositories.LabelCrudRepository
import ru.kyamshanov.mission.point.database.repositories.OrderedTaskQueryRepository
import ru.kyamshanov.mission.point.domain.models.TaskEntity
import ru.kyamshanov.mission.point.database.repositories.TaskCrudRepository
import ru.kyamshanov.mission.point.database.repositories.TaskLabelCrudRepository
import ru.kyamshanov.mission.point.domain.models.TaskLabelEntity
import ru.kyamshanov.mission.point.domain.models.TaskOrderEntity
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.network.dtos.*
import ru.kyamshanov.mission.point.utils.MovableLinkedList
import ru.kyamshanov.mission.point.utils.MutablePair
import java.time.LocalDateTime

@RestController
@RequestMapping("/point/private/label")
class LabelPrivateController(
    private val taskCrudRepository: TaskCrudRepository,
    private val taskLabelCrudRepository: TaskLabelCrudRepository,
    private val labelCrudRepository: LabelCrudRepository,
) {


    @PatchMapping("/set")
    suspend fun createTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: SetLabelRqDto
    ): ResponseEntity<Unit> {
        assert(taskCrudRepository.findById(body.taskId)?.owner == userId) { "User has not this task" }
        val taskLabels = taskLabelCrudRepository.findAllByTaskId(body.taskId).toCollection(mutableListOf())
            .associate { it.labelId to false }.toMutableMap()
        body.labels.forEach {
            taskLabels.computeIfPresent(it) { _, _ -> true }
                ?: taskLabelCrudRepository.save(TaskLabelEntity(body.taskId, it))
        }
        taskLabels.entries.forEach {
            val (labelId, isUse) = it.key to it.value
            if (!isUse) taskLabelCrudRepository.deleteByTaskIdAndLabelId(
                body.taskId,
                labelId
            )
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/all")
    suspend fun getAllLabels(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
    ): ResponseEntity<LabelRsDto> {
        val labels = labelCrudRepository.findAllByOwner(userId).toCollection(mutableListOf()).map { it.toDto() }
        return ResponseEntity(LabelRsDto(labels), HttpStatus.OK)
    }

}
