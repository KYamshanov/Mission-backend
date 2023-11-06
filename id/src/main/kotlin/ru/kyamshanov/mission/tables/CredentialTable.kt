package ru.kyamshanov.mission.tables

import org.jetbrains.exposed.dao.id.UUIDTable


/**
 * CREATE TABLE "mission-id".credential
 * (
 *     user_id  VARCHAR(32) PRIMARY KEY REFERENCES "mission-id".users (id),
 *     username VARCHAR(30)  NOT NULL UNIQUE,
 *     password VARCHAR(500) NOT NULL
 * );
 */
object CredentialTable : UUIDTable(name = "mission-id.credential", columnName = "user_id") {
    val username = varchar("username", 30)
    val password = varchar("password", 500)
}