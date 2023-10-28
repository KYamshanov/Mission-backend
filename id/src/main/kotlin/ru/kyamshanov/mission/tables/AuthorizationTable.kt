package ru.kyamshanov.mission.tables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

object AuthorizationTable : UUIDTable(name = "mission-id.authorization") {
    val service = varchar("service", 6)
    val authCode = varchar("auth_code", 128)
    val scopes = varchar("scopes", 1000)
    val serviceMetadata = json<ServiceMetadata>("service_metadata", Json { prettyPrint = true })
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val codeChallenge = varchar("code_challenge", 43)
    val enabled = bool("enabled")
    val accessToken = varchar("access_token", 1000)
    val refreshToken = varchar("refresh_token", 500)
    val refreshTokenExpiresAt = datetime("refresh_token_expires_at")
}

@Serializable
data class ServiceMetadata(val token: String?)