package ru.kyamshanov.mission.tables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

/**
 * CREATE TABLE "mission-id".users
 * (
 *     id      VARCHAR(32) PRIMARY KEY,
 *     enabled BOOLEAN NOT NULL
 * );
 *
 */
object UsersTable : UUIDTable(name = "mission-id.users") {
    val enabled = bool("enabled")
}