package ru.kyamshanov.mission.point.network

import kotlinx.coroutines.flow.toCollection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.kyamshanov.mission.point.domain.models.TaskEntity
import ru.kyamshanov.mission.point.database.repositories.TaskCrudRepository
import ru.kyamshanov.mission.point.domain.models.TaskType
import ru.kyamshanov.mission.point.network.dtos.AttachedTasksResponseDto
import ru.kyamshanov.mission.point.network.dtos.CreateTaskRequestDto
import ru.kyamshanov.mission.point.network.dtos.CreateTaskResponseDto
import ru.kyamshanov.mission.point.network.dtos.GetTaskRsDto
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@RestController
@RequestMapping("/point/private/")
class PrivateController(
    private val taskCrudRepository: TaskCrudRepository
) {

    @GetMapping("/attached")
    suspend fun getAttachedTasks(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
    ): ResponseEntity<AttachedTasksResponseDto> {
        return ResponseEntity(
            AttachedTasksResponseDto(
                taskCrudRepository.getAllByOwner(userId).toCollection(mutableListOf())
                    .map {
                        AttachedTasksResponseDto.TaskSlim(
                            id = it.id,
                            title = it.title,
                            creationTime = it.creationTime,
                            completionTime = it.completionTime,
                            priority = it.priority,
                            status = it.status,
                            type = it.type,
                        )
                    }),
            HttpStatus.OK
        )
    }

    @PostMapping("/create")
    suspend fun createTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: CreateTaskRequestDto
    ): ResponseEntity<CreateTaskResponseDto> {
        val entity = TaskEntity(
            title = body.title,
            description = body.description,
            creationTime = LocalDateTime.now(),
            owner = userId
        )

        return ResponseEntity(
            CreateTaskResponseDto(taskCrudRepository.save(entity).id),
            HttpStatus.OK
        )
    }

    @GetMapping("/get")
    suspend fun getTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true) id: String
    ): ResponseEntity<GetTaskRsDto> {
        return ResponseEntity(
            (taskCrudRepository.findById(id) ?: throw IllegalArgumentException("Task was not found"))
                .let {
                    GetTaskRsDto(
                        id = it.id,
                        title = it.title,
                        creationTime = it.creationTime,
                        completionTime = it.completionTime,
                        priority = it.priority,
                        status = it.status,
                        description = it.description,
                        updateTime = it.updateTime,
                        type = it.type
                    )
                },
            HttpStatus.OK
        )
    }

    @DeleteMapping("/delete")
    suspend fun deleteTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "id") taskId: String
    ): ResponseEntity<Unit> {
        taskCrudRepository.deleteByIdAndOwner(taskId, userId)
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/type")
    @Transactional
    suspend fun setTaskType(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "id") taskId: String,
        @RequestParam(required = true, name = "type") taskType: TaskType
    ): ResponseEntity<Unit> {
        taskCrudRepository.updateTaskType(taskId, userId, taskType).toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Less or more one task have been updated. Count: ${it.size}" } }
        return ResponseEntity(HttpStatus.OK)
    }
}
