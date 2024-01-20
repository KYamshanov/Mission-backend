package ru.kyamshanov.mission.utils

import io.ktor.server.config.*
import java.nio.file.Files
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.readLines


private val secrets: Map<String, String> =
    Files.walk(Path("/run/secrets"))
        .filter { item -> Files.isRegularFile(item) }
        .map { item ->
            val variableName = item.name
            val fileValue = item.readLines().joinToString("\n")
            variableName to fileValue
        }.collect(Collectors.toMap({ key -> key.first }, { value -> value.second }))


/**
 * Support for obtain property using environment or get sensitive data using docker secrets
 */
fun ApplicationConfig.secret(path: String) =
    property(path).getString().let {
        if (it.startsWith("SECRET_")) getSecret(it.removePrefix("SECRET_"))
        else it
    }

private fun getSecret(secretKey: String) =
    secrets[secretKey] ?: throw IllegalStateException("Secret has not found by key $secretKey")