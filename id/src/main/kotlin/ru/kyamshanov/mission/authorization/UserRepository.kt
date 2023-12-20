package ru.kyamshanov.mission.authorization

import java.util.*

interface UserRepository {

    suspend fun findUserById(userId: UUID): UserModel

    suspend fun getInternalUserIdBySocialServiceId(id: String): UUID
}