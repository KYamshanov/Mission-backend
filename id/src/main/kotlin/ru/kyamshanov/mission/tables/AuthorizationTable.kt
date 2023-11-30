package ru.kyamshanov.mission.tables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import ru.kyamshanov.mission.client.models.SocialService

/**
 * CREATE TABLE "mission-id".authorization
 * (
 *     id                             VARCHAR(32) PRIMARY KEY,
 *     client_id                      VARCHAR(32)   NOT NULL REFERENCES "mission-id".clients (id),
 *     user_id                        VARCHAR(32)   NOT NULL REFERENCES "mission-id".users (id),
 *     authorization_grant_type       VARCHAR(200)  NOT NULL,
 *     authorization_metadata         JSON DEFAULT NULL,
 *     authentication_code            VARCHAR(128)  NOT NULL,
 *     authentication_code_expires_at TIMESTAMP     NOT NULL,
 *     user_metadata                  JSON          NOT NULL,
 *     scopes                         VARCHAR(1000) NOT NULL,
 *     access_token_value             VARCHAR(4000),
 *     access_token_issued_at         TIMESTAMP     NOT NULL,
 *     access_token_expires_at        TIMESTAMP     NOT NULL,
 *     refresh_token_value            VARCHAR(4000),
 *     refresh_token_issued_at        TIMESTAMP     NOT NULL,
 *     refresh_token_expires_at       TIMESTAMP     NOT NULL
 * );
 */
object AuthorizationTable : UUIDTable(name = "mission-id.authorization") {
    val clientId = varchar("client_id", 20)
    val userId = uuid("user_id")
    val issuedAt = datetime("issued_at")
    val authorizationGrantType = varchar("authorization_grant_type", 200)
    val authorizationMetadata = json<AuthorizationMetadata>("authorization_metadata", Json { prettyPrint = true })
    val authenticationCode = varchar("authentication_code", 128)
    val authenticationCodeExpiresAt = datetime("authentication_code_expires_at")
    val userMetadata = json<UserMetadata>("user_metadata", Json { prettyPrint = true })
    val scopes = varchar("scopes", 1000)
    val accessTokenValue = varchar("access_token_value", 4000)
    val accessTokenIssuedAt = datetime("access_token_issued_at")
    val accessTokenExpiresAt = datetime("access_token_expires_at")
    val refreshTokenValue = varchar("refresh_token_value", 4000)
    val refreshTokenIssuedAt = datetime("refresh_token_issued_at")
    val refreshTokenExpiresAt = datetime("refresh_token_expires_at")
    val socialService = socialServiceEnumeration("social_service")
}

@Serializable
data class AuthorizationMetadata(val codeChallenge: String, val token: String)

@Serializable
data class UserMetadata(val sessionId: String)