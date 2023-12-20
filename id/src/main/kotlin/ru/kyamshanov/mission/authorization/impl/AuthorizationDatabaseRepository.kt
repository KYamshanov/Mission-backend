package ru.kyamshanov.mission.authorization.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.kyamshanov.mission.authorization.AuthorizationModel
import ru.kyamshanov.mission.authorization.AuthorizationRepository
import ru.kyamshanov.mission.tables.AuthorizationMetadata
import ru.kyamshanov.mission.tables.AuthorizationTable
import java.time.LocalDateTime


/**
 * Repository based on database storage
 */
class AuthorizationDatabaseRepository : AuthorizationRepository {

    override suspend fun findFirstByAuthorizationCode(authorizationCode: String): AuthorizationModel =
        withContext(Dispatchers.IO) {
            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.authenticationCode eq authorizationCode
                }.limit(1).single()
            }.obtainAuthorizationModel()
        }

    override suspend fun insert(authorization: AuthorizationModel): Unit = withContext(Dispatchers.IO) {
        transaction {
            AuthorizationTable.insert {
                it[socialService] = authorization.socialService
                it[clientId] = authorization.clientId
                it[issuedAt] = authorization.issuedAt
                it[authenticationCode] = authorization.authorizationCode
                it[authenticationCodeExpiresAt] = authorization.authenticationCodeExpiresAt
                it[scopes] = authorization.scopes
                it[authorizationMetadata] = authorization.metadata?.let { metadata ->
                    AuthorizationMetadata(
                        codeChallenge = metadata.codeChallenge,
                        token = metadata.encryptedToken
                    )
                }
                it[userId] = authorization.userId
            }
        }
    }

    override suspend fun updateAuthData(authorization: AuthorizationModel): Unit = withContext(Dispatchers.IO) {
        transaction {
            AuthorizationTable.update({ AuthorizationTable.id eq authorization.authorizationId }) {
                it[userId] = authorization.userId
                it.putTokens(authorization)
            }.also {
                check(it == 1) { "There is was updated not one entity in the database at refreshing. [$it]" }
            }
        }
    }

    override suspend fun updateTokens(authorization: AuthorizationModel): Unit = withContext(Dispatchers.IO) {
        transaction {
            AuthorizationTable.update({ AuthorizationTable.id eq authorization.authorizationId }) {
                it.putTokens(authorization)
            }.also {
                check(it == 1) { "There is was updated not one entity in the database at refreshing. [$it]" }
            }
        }
    }

    private fun UpdateStatement.putTokens(authorization: AuthorizationModel) {
        set(AuthorizationTable.accessTokenIssuedAt, authorization.accessTokenIssuedAt)
        set(AuthorizationTable.accessTokenExpiresAt, authorization.accessTokenExpiresAt)
        set(AuthorizationTable.accessTokenValue, authorization.accessToken)
        if (authorization.refreshToken != null) {
            set(AuthorizationTable.refreshTokenValue, authorization.refreshToken)
            set(AuthorizationTable.refreshTokenIssuedAt, LocalDateTime.now())
        }
        if (authorization.refreshTokenExpiresAt != null) {
            set(AuthorizationTable.refreshTokenExpiresAt, authorization.refreshTokenExpiresAt)
        }
        if (authorization.refreshTokenIssuedAt != null) {
            set(AuthorizationTable.refreshTokenIssuedAt, authorization.refreshTokenIssuedAt)
        }
    }

    override suspend fun findFirstByRefreshToken(refreshToken: String): AuthorizationModel =
        withContext(Dispatchers.IO) {
            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.refreshTokenValue eq refreshToken
                }.limit(1).single()
            }.obtainAuthorizationModel()
        }

    private fun ResultRow.obtainAuthorizationModel() = AuthorizationModel(
        authorizationCode = get(AuthorizationTable.authenticationCode),
        clientId = get(AuthorizationTable.clientId),
        scopes = get(AuthorizationTable.scopes),
        socialService = get(AuthorizationTable.socialService),
        authorizationId = get(AuthorizationTable.id).value,
        authenticationCodeExpiresAt = get(AuthorizationTable.authenticationCodeExpiresAt),
        accessTokenExpiresAt = get(AuthorizationTable.accessTokenExpiresAt),
        accessToken = get(AuthorizationTable.accessTokenValue),
        refreshToken = get(AuthorizationTable.refreshTokenValue),
        refreshTokenExpiresAt = get(AuthorizationTable.refreshTokenExpiresAt),
        metadata = get(AuthorizationTable.authorizationMetadata)?.let {
            AuthorizationModel.Metadata(
                codeChallenge = it.codeChallenge,
                encryptedToken = it.token
            )
        },
        issuedAt = get(AuthorizationTable.issuedAt),
        userId = get(AuthorizationTable.userId),
        refreshTokenIssuedAt = get(AuthorizationTable.refreshTokenIssuedAt),
        accessTokenIssuedAt = get(AuthorizationTable.accessTokenIssuedAt)
    )

}