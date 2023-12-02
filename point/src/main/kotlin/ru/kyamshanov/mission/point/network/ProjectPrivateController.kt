package ru.kyamshanov.mission.point.network

import kotlinx.coroutines.flow.toCollection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.kyamshanov.mission.point.database.repositories.LabelCrudRepository
import ru.kyamshanov.mission.point.database.repositories.ProjectCrudRepository
import ru.kyamshanov.mission.point.domain.models.*
import ru.kyamshanov.mission.point.network.dtos.*
import java.util.Random

@RestController
@RequestMapping("/point/private/project")
class ProjectPrivateController(
    private val projectCrudRepository: ProjectCrudRepository,
    private val labelCrudRepository: LabelCrudRepository
) {

    @GetMapping("/all")
    suspend fun getProjects(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
    ): ResponseEntity<AllProjectsRsDto> {
        val projects: List<AllProjectsRsDto.ProjectSlimDto> =
            projectCrudRepository.getAllByOwner(userId).toCollection(mutableListOf()).map { it.toDto() }
        return ResponseEntity(AllProjectsRsDto(projects), HttpStatus.OK)
    }

    @PostMapping("/create")
    @Transactional
    suspend fun createProject(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestBody(required = true) body: CreateProjectsRqDto,
    ): ResponseEntity<CreateProjectsRsDto> {
        val savedProject = projectCrudRepository.save(ProjectEntity(body.title, body.description, userId))
        labelCrudRepository.save(
            LabelEntity(
                body.title,
                userId,
                savedProject.id,
                Random().nextLong(3642227)+ 4289926693
            )
        )
        return ResponseEntity(CreateProjectsRsDto(savedProject.id), HttpStatus.OK)
    }

    @GetMapping("/get")
    @Transactional
    suspend fun getProject(
        @RequestHeader(value = "\${USER_ID_HEADER_KEY}", required = true) userId: String,
        @RequestParam(required = true, name = "id") projectId: String
    ): ResponseEntity<GetProjectRsDto> {
        val project = requireNotNull(projectCrudRepository.findByIdAndOwner(projectId, userId)) { "Project not found" }
        val labels = labelCrudRepository.findAllByProjectId(projectId).toCollection(mutableListOf()).map { it.toDto() }
        return ResponseEntity(project.toGetProjectRsDto(labels), HttpStatus.OK)
    }
}

private fun ProjectEntity.toGetProjectRsDto(labels: List<LabelDto>) = GetProjectRsDto(
    id, title, description, EditingProjectRulesDto(true), labels
)

private fun ProjectEntity.toDto() = AllProjectsRsDto.ProjectSlimDto(id, title)
