package ru.kyamshanov.mission.point.network

import kotlinx.coroutines.flow.toCollection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.kyamshanov.mission.point.database.repositories.*
import ru.kyamshanov.mission.point.network.dtos.*

@RestController
@RequestMapping("/point/private/")
class SearchPrivateController(
    private val taskCrudRepository: TaskCrudRepository,
    private val projectCrudRepository: ProjectCrudRepository,
    private val labelQueryRepository: LabelQueryRepository
) {


    @GetMapping("/search")
    @Transactional
    suspend fun search(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "value") searchPhrase: String,
    ): ResponseEntity<SearchRsDto> {
        val tasks = taskCrudRepository.findByTitleContainingAndOwner(searchPhrase, userId).toCollection(mutableListOf())
            .map {
                SearchRsDto.TaskSlim(
                    id = it.id,
                    title = it.title,
                    creationTime = it.creationTime,
                    completionTime = it.completionTime,
                    priority = it.priority,
                    status = it.status,
                    type = it.type.toDto(),
                )
            }
        val projects = projectCrudRepository.findByTitleContainingAndOwner(searchPhrase, userId)
            .toCollection(mutableListOf())
            .map {
                SearchRsDto.ProjectSlim(
                    id = it.id,
                    title = it.title,
                )
            }
        return ResponseEntity(
            SearchRsDto(
                tasks, projects
            ),
            HttpStatus.OK
        )
    }

    @PostMapping("/search")
    @Transactional
    suspend fun searchWithLabels(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: SearchRqDto,
    ): ResponseEntity<SearchRsDto> {
        val tasks = labelQueryRepository.selectTasksByLabelAndSearchPhrase(body.labels, body.searchPhrase, userId)
            .toCollection(mutableListOf())
            .map {
                SearchRsDto.TaskSlim(
                    id = it.id,
                    title = it.title,
                    creationTime = it.creationTime,
                    completionTime = it.completionTime,
                    priority = it.priority,
                    status = it.status,
                    type = it.type.toDto(),
                )
            }
        val projects = labelQueryRepository.selectProjectsByLabelAndSearchPhrase(body.labels, body.searchPhrase, userId)
            .toCollection(mutableListOf())
            .map {
                SearchRsDto.ProjectSlim(
                    id = it.id,
                    title = it.title,
                )
            }
        return ResponseEntity(
            SearchRsDto(
                tasks, projects
            ),
            HttpStatus.OK
        )
    }

}