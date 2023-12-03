package ru.kyamshanov.mission.point.network

import kotlinx.coroutines.flow.toCollection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.database.repositories.*
import ru.kyamshanov.mission.point.domain.models.*
import ru.kyamshanov.mission.point.network.dtos.*
import ru.kyamshanov.mission.point.utils.MovableLinkedList
import ru.kyamshanov.mission.point.utils.MutablePair
import java.time.LocalDateTime

@RestController
@RequestMapping("/point/private/")
class PrivateController(
    private val taskCrudRepository: TaskCrudRepository,
    private val taskOrderCrudRepository: OrderedTaskQueryRepository,
    private val labelCrudRepository: LabelCrudRepository,
    private val labelQueryRepository: LabelQueryRepository,
    private val taskLabelCrudRepository: TaskLabelCrudRepository
) {

    @GetMapping("/attached")
    suspend fun getAttachedTasks(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
    ): ResponseEntity<AttachedTasksResponseDto> {
        //TODO Рефакторинг. Also see: https://github.com/KYamshanov/Mission-backend/issues/19
        val tasksMap = taskCrudRepository.getAllByOwner(userId).toCollection(mutableListOf()).associateBy { it.id }
        val orderedTasks = if (tasksMap.isNotEmpty()) {
            val order = taskOrderCrudRepository.selectAll(userId).toCollection(mutableListOf())
            val sortedTasks = tasksMap.values.toMutableList().apply {
                sortWith(
                    compareBy<TaskEntity> { it.status.weight }
                        .thenBy { it.priority?.weight ?: 1 }
                        .thenBy { it.creationTime }
                )
            }

            val linkedList = MovableLinkedList(sortedTasks.map { it.id })
            val orderN = order.map { MutablePair(it, true) }.associateBy { it.first.next }.toMutableMap()

            orderN.forEach { t: String?, u ->
                if (u.second) muve(linkedList, u, orderN)
            }


            //order.forEach { if (it.next == null) linkedList.moveInTail(it.id) else linkedList.move(it.id, it.next) }
            linkedList.getList().map { requireNotNull(tasksMap[it]) }
        } else emptyList()

        return ResponseEntity(
            AttachedTasksResponseDto(
                orderedTasks
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

    private fun muve(
        linkedList: MovableLinkedList<String>,
        taskPair: MutablePair<TaskOrderEntity, Boolean>,
        map: MutableMap<String?, MutablePair<TaskOrderEntity, Boolean>>
    ) {
        val taskEntity = taskPair.first
        println("Muve ${taskEntity.id} ${taskEntity.next}")
        if (taskEntity.next == null) linkedList.moveInTail(taskEntity.id) else linkedList.move(
            taskEntity.id,
            taskEntity.next
        )
        taskPair.second = false
        map[taskEntity.id]?.let { muve(linkedList, it, map) }
    }

    @PostMapping("/order")
    suspend fun setOrderOfTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: RequestOrderTaskDto
    ): ResponseEntity<Unit> {
        assert(taskCrudRepository.getFirstByOwnerAndId(userId, body.taskId) != null) { "Task not found" }
        taskOrderCrudRepository.orderTask(body)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/create")
    @Transactional
    suspend fun createTask(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: CreateTaskRequestDto
    ): ResponseEntity<CreateTaskResponseDto> {
        val entity = TaskEntity(
            title = body.title,
            description = body.description,
            creationTime = LocalDateTime.now(),
            owner = userId
        ).let { taskCrudRepository.save(it) }

        if (body.label != null) {
            require(labelCrudRepository.findById(body.label)?.owner == userId) { "Label owner must be request user" }
            taskLabelCrudRepository.save(TaskLabelEntity(entity.id, body.label))
        }

        return ResponseEntity(
            CreateTaskResponseDto(entity.id),
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
                        editingRules = EditingRulesDto(isEditable = it.owner == userId),
                        labels = labelQueryRepository.selectByTaskId(it.id, userId).toCollection(mutableListOf())
                            .map { it.toDto() }
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
        taskOrderCrudRepository.removeOrder(taskId)
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
        taskOrderCrudRepository.removeOrder(taskId)
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
        taskOrderCrudRepository.removeOrder(taskId)
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
        taskOrderCrudRepository.removeOrder(taskId)
        return ResponseEntity(HttpStatus.OK)
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

fun LabelEntity.toDto() = LabelDto(id, title, color)
