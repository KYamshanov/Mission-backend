package ru.kyamshanov.mission.authorization

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import ru.kyamshanov.mission.client.models.SocialService

/**
 * Interface of intercept for ktor request to process OAuth authorization
 */
interface AuthInterceptor {

    suspend fun authorize(pipeline: PipelineContext<Unit, ApplicationCall>)

    suspend fun token(pipeline: PipelineContext<Unit, ApplicationCall>)

    suspend fun loginBy(service: SocialService, call: ApplicationCall)

    suspend fun authorizedBy(service: SocialService, pipeline: PipelineContext<Unit, ApplicationCall>)

    suspend fun logout(pipeline: PipelineContext<Unit, ApplicationCall>)
}