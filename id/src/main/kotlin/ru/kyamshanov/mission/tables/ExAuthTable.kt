package ru.kyamshanov.mission.tables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import ru.kyamshanov.mission.client.models.SocialService


/**
 * CREATE TABLE "mission-id".ex_auth
 * (
 *     user_id          VARCHAR(32) PRIMARY KEY REFERENCES "mission-id".users (id),
 *     social_service   VARCHAR(10)  NOT NULL,
 *     external_user_id VARCHAR(128) NOT NULL
 * );
 */
object ExAuthTable : Table(name = "mission-id.ex_auth") {
    val userId = varchar("user_id", 36)
    val socialService = socialServiceEnumeration("social_service_id")
    val externalUserId = varchar("external_user_id", 128)
}