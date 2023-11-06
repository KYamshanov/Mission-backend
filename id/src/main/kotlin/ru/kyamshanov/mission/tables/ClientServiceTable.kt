package ru.kyamshanov.mission.tables

import org.jetbrains.exposed.sql.Table


/**
 * CREATE TABLE "mission-id".client_service
 * (
 *     client_id         VARCHAR(36) NOT NULL REFERENCES "mission-id".clients (id),
 *     social_service_id VARCHAR(36) NOT NULL REFERENCES "mission-id".social_service (id)
 * );
 */
object ClientServiceTable : Table(name = "mission-id.client_service") {

    val clientId = varchar("client_id", 36)
    val socialServiceId = varchar("social_service_id", 36)
}