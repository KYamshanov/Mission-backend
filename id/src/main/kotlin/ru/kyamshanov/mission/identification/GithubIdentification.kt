package ru.kyamshanov.mission.identification

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.kyamshanov.mission.authorization.UserRepository
import ru.kyamshanov.mission.dto.GithubUserRsDto
import java.util.*

class GithubIdentification(
    private val httpClient: HttpClient,
    private val userRepository: UserRepository,
    private val clientId: String,
    private val clientSecret: String
) : GithubIdentificationService {


    override suspend fun identify(accessToken: String): UUID {
        val githubUserId = httpClient.get("https://api.github.com/user") {
            header("Accept", "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
            header("Authorization", "Bearer $accessToken")
        }.body<GithubUserRsDto>().id.toString()

        return userRepository.getInternalUserIdBySocialServiceId(githubUserId)
    }

    override suspend fun revoke(accessToken: String) {
        val response = httpClient.delete("https://api.github.com/applications/$clientId/token") {
            header("Accept", "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
            header(
                "Authorization",
                "Basic ${Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())}"
            )
            setBody("{\"access_token\":\"$accessToken\"}")
        }

        check(response.status == HttpStatusCode.NoContent) { "Github did not revoke token. Status ${response.status}" }
    }
}