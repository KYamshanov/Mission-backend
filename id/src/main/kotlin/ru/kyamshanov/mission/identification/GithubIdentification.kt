package ru.kyamshanov.mission.identification

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.GithubUserRsDto
import ru.kyamshanov.mission.tables.ExAuthTable
import ru.kyamshanov.mission.tables.UsersTable
import java.util.*

class GithubIdentification(
    private val httpClient: HttpClient,
) : GithubIdentificationService {


    override suspend fun identify(githubAccessToken: String): UUID {
        val githubUserId = httpClient.get("https://api.github.com/user") {
            header("Accept", "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
            header("Authorization", "Bearer $githubAccessToken")
        }.body<GithubUserRsDto>().id.toString()


        return transaction {
            ExAuthTable.select { ExAuthTable.externalUserId eq githubUserId }.limit(1).firstOrNull()?.let {
                it[ExAuthTable.userId]
            } ?: run {
                val userId: UUID = UsersTable.insert { it[enabled] = true }[UsersTable.id].value
                ExAuthTable.insert {
                    it[ExAuthTable.userId] = userId
                    it[socialService] = SocialService.GITHUB
                    it[externalUserId] = githubUserId
                }
                userId
            }
        }
    }
}
