package ru.kyamshanov.mission.client

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

sealed interface AuthDelegate {

    suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient)
}

interface AuthorizeDelegate : AuthDelegate

interface TokenDelegate : AuthDelegate

interface AuthorizedDelegate : AuthDelegate