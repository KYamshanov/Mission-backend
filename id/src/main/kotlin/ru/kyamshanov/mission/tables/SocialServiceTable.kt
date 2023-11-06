package ru.kyamshanov.mission.tables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.json.json

/**
 * CREATE TABLE "mission-id".social_service
 * (
 *     id            VARCHAR(36) PRIMARY KEY,
 *     code          VARCHAR(10) NOT NULL,
 *     configuration json        NOT NULL
 * );
 */
object SocialServiceTable : UUIDTable(name = "mission-id.social_service") {
    val code = bool("code")
    val configuration = json<SocialServiceConfig>("configuration", Json { prettyPrint = true })
}

@Serializable
data class SocialServiceConfig(val somethingData: String)