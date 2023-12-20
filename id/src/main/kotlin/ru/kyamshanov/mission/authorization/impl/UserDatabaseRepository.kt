package ru.kyamshanov.mission.authorization.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.authorization.UserModel
import ru.kyamshanov.mission.authorization.UserRepository
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.tables.ExAuthTable
import ru.kyamshanov.mission.tables.UsersTable
import java.util.*

class UserDatabaseRepository : UserRepository {
    override suspend fun findUserById(userId: UUID): UserModel = withContext(Dispatchers.IO) {
        val isEnabled = transaction {
            UsersTable.select { UsersTable.id eq userId }.single().let { it[UsersTable.enabled] }
        }
        UserModel(userId, isEnabled)
    }

    override suspend fun getInternalUserIdBySocialServiceId(id: String): UUID = withContext(Dispatchers.IO) {
        transaction {
            ExAuthTable.select { ExAuthTable.externalUserId eq id }.limit(1).firstOrNull()?.let {
                it[ExAuthTable.userId]

            } ?: run {
                val userId: UUID = UsersTable.insert { it[enabled] = true }[UsersTable.id].value
                ExAuthTable.insert {
                    it[ExAuthTable.userId] = userId
                    it[socialService] = SocialService.GITHUB
                    it[externalUserId] = id
                }
                userId
            }
        }
    }

}
