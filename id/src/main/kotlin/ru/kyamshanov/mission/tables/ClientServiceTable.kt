package ru.kyamshanov.mission.tables

import org.jetbrains.exposed.sql.Table
import ru.kyamshanov.mission.client.models.SocialService


/**
 * CREATE TABLE "mission-id".client_service
 * (
 *     client_id         VARCHAR(36) NOT NULL REFERENCES "mission-id".clients (id),
 *     social_service_id VARCHAR(36) NOT NULL REFERENCES "mission-id".social_service (id)
 * );
 */
object ClientServiceTable : Table(name = "mission-id.client_service") {

    val clientId = varchar("client_id", 20)
    val socialServiceId = socialServiceEnumeration("social_service_id")
    val enabled = bool("enabled")
}