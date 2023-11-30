package ru.kyamshanov.mission.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ru.kyamshanov.mission.tables.Authorities.entityId


/**
 * CREATE TABLE "mission-id".user_authorities
 * (
 *     user_id      VARCHAR(32) PRIMARY KEY REFERENCES "mission-id".users (id),
 *     authority_id VARCHAR(16) NOT NULL REFERENCES "mission-id".authorities (id)
 * );
 */
object UserAuthorities : Table(name = "mission-id.user_authorities") {
    val userId = uuid("user_id")
    val authorityId = uuid("authority_id")
}