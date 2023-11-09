package ru.kyamshanov.mission.identification

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.kyamshanov.mission.dto.GithubUserRsDto

class GithubIdentification(
    private val httpClient: HttpClient
) : GithubIdentificationService {


    override suspend fun identify(githubAccessToken: String): String {
        val githubUserInfo = httpClient.get("https://api.github.com/user") {
            header("Accept", "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
            header("Authorization", "Bearer $githubAccessToken")
        }.body<GithubUserRsDto>()
        return githubUserInfo.login
    }
}
