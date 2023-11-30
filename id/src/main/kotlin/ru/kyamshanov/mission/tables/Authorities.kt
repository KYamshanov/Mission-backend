package ru.kyamshanov.mission.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table


/**
 *
 * CREATE TABLE "mission-id".authorities
 * (
 *     id     VARCHAR(16) PRIMARY KEY,
 *     scopes VARCHAR(500) NOT NULL
 * );
 */
object Authorities : UUIDTable(name = "mission-id.authorities") {
    val scopes = varchar("scopes", 500)
}