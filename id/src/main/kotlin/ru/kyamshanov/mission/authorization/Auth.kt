package ru.kyamshanov.mission.authorization

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.models.SocialService

interface Auth {

    suspend fun authorize(pipeline: PipelineContext<Unit, ApplicationCall>)

    suspend fun token(pipeline: PipelineContext<Unit, ApplicationCall>)

    suspend fun authorizedBy(service: SocialService, pipeline: PipelineContext<Unit, ApplicationCall>)
}