package ru.kyamshanov.mission.point.network

import kotlinx.coroutines.flow.toCollection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskEntity
import ru.kyamshanov.mission.point.database.repositories.TaskCrudRepository
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.network.dtos.*
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
                            type = it.type.toDto(),
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
                        type = it.type.toDto(),
                        editingRules = EditingRulesDto(isEditable = it.owner == userId)
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
        @RequestParam(required = true, name = "type") taskType: TaskTypeDto
    ): ResponseEntity<Unit> {
        taskCrudRepository.updateTaskType(taskId, userId, taskType.toDomain()).toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Less or more one task have been updated. Count: ${it.size}" } }
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/status")
    @Transactional
    suspend fun setTaskStatus(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "id") taskId: String,
        @RequestParam(required = true, name = "status") taskStatus: TaskStatus
    ): ResponseEntity<Unit> {
        taskCrudRepository.updateTaskStatus(taskId, userId, taskStatus).toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Less or more one task have been updated. Count: ${it.size}" } }
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/priority")
    @Transactional
    suspend fun setPriority(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "id") taskId: String,
        @RequestParam(required = true, name = "value") priority: TaskPriority
    ): ResponseEntity<Unit> {
        taskCrudRepository.updateTaskPriority(taskId, userId, priority).toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Less or more one task have been updated. Count: ${it.size}" } }
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/priority")
    @Transactional
    suspend fun removePriority(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "id") taskId: String
    ): ResponseEntity<Unit> {
        taskCrudRepository.updateTaskPriority(taskId, userId, null).toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Less or more one task have been updated. Count: ${it.size}" } }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/search")
    @Transactional
    suspend fun search(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "value") searchPhrase: String
    ): ResponseEntity<AttachedTasksResponseDto> {
        return ResponseEntity(
            AttachedTasksResponseDto(
                taskCrudRepository.findByTitleContainingAndOwner(searchPhrase, userId).toCollection(mutableListOf())
                    .map {
                        AttachedTasksResponseDto.TaskSlim(
                            id = it.id,
                            title = it.title,
                            creationTime = it.creationTime,
                            completionTime = it.completionTime,
                            priority = it.priority,
                            status = it.status,
                            type = it.type.toDto(),
                        )
                    }),
            HttpStatus.OK
        )
    }

    @PatchMapping("/edit")
    @Transactional
    suspend fun editTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: EditTaskRsDto
    ): ResponseEntity<Unit> {
        when {
            body.title != null && body.description != null -> {
                taskCrudRepository.updateTaskTitleDescription(userId, body.id, body.title, body.description)
            }

            body.title != null -> {
                taskCrudRepository.updateTaskTitle(userId, body.id, body.title)
            }

            body.description != null -> {
                taskCrudRepository.updateTaskDescription(userId, body.id, body.description)
            }

            else -> throw IllegalArgumentException("Title and description together cannot be null")
        }.toCollection(mutableListOf())
            .also { assert(it.size == 1) { "Less or more one task have been updated. Count: ${it.size}" } }
        return ResponseEntity(HttpStatus.OK)
    }
}
