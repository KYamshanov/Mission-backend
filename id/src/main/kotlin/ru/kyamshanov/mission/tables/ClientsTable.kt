package ru.kyamshanov.mission.tables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

/**
 * CREATE TABLE "mission-id".clients
 * (
 *     id                            VARCHAR(32) PRIMARY KEY,
 *     redirect_url                  VARCHAR(200),
 *     scopes                        VARCHAR(1000) NOT NULL,
 *     issued_at                     TIMESTAMP DEFAULT (now()),
 *     enabled                       BOOLEAN       NOT NULL,
 *     client_authentication_methods VARCHAR(200)  NOT NULL,
 *     grand_types                   VARCHAR(1000) NOT NULL,
 *     expires_at                    TIMESTAMP DEFAULT NULL,
 *     logout_redirect_url           VARCHAR(200),
 *     auth_systems                  VARCHAR(200)  NOT NULL,
 *     metadata                      JSON      DEFAULT NULL
 * );
 */
object ClientsTable : IdTable<String>(name = "mission-id.clients") {
    override val id: Column<EntityID<String>> = varchar("id", 20).entityId()
    override val primaryKey = PrimaryKey(id)

    val redirectUrl = varchar("redirect_url", 200)
    val scopes = varchar("scopes", 1000)
    val issuedAt = datetime("issued_at")
    val enabled = bool("enabled")
    val clientAuthenticationMethods = varchar("client_authentication_methods", 200)
    val grand_types = varchar("grand_types", 1000)
    val response_types = varchar("response_types", 1000)
    val expiresAt = datetime("expires_at")
    val logoutRedirectUrl = varchar("logout_redirect_url", 200)
    val metadata = json<ClientMetadata,>("metadata", Json { prettyPrint = true })
}

@Serializable
data class ClientMetadata(
    val accessTokenLifetimeInMS: Long ,
    val refreshTokenLifetimeInMS: Long,
    val authenticationCodeLifeTimeInMs: Long
)