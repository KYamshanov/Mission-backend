package ru.kyamshanov.mission.authorization

interface AuthorizationRepository {

    @Throws(NoSuchElementException::class)
    suspend fun findFirstByAuthorizationCode(authorizationCode: String): AuthorizationModel

    suspend fun findFirstByRefreshToken(refreshToken: String): AuthorizationModel

    suspend fun insert(authorization: AuthorizationModel)

    suspend fun updateAuthData(authorization: AuthorizationModel)

    suspend fun updateTokens(authorization: AuthorizationModel)

    suspend fun findFirstByAccessToken(accessToken: String): AuthorizationModel

    suspend fun disableAuthorizationByRefreshToken(refreshToken: String)
}