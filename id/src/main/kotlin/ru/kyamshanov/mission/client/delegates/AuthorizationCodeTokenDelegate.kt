package ru.kyamshanov.mission.client.delegates

import io.jsonwebtoken.Jwts
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.dto.GithubUserRsDto
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.plugins.*
import ru.kyamshanov.mission.tables.AuthorizationTable

class AuthorizationCodeTokenDelegate(
    private val issuerUrl: String
) : TokenDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val formParameters = call.receiveParameters()
            val authCode: String = requireNotNull(formParameters["code"])
            val codeVerifier: String = requireNotNull(formParameters["code_verifier"])
            val codeChallenge: String
            val token: String
            val scopes: String


            println("authCode")

            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.authenticationCode eq authCode
                }.limit(1).single()
            }.also {
                codeChallenge = it[AuthorizationTable.authorizationMetadata].codeChallenge
                token = requireNotNull(it[AuthorizationTable.authorizationMetadata].token)
                scopes = it[AuthorizationTable.scopes]
            }

            assert(getCodeChallenge(codeVerifier) == codeChallenge)

            val githubUserInfo = httpClient.get("https://api.github.com/user") {
                header("Accept", "application/vnd.github+json")
                header("X-GitHub-Api-Version", "2022-11-28")
                header("Authorization", "Bearer $token")
            }.body<GithubUserRsDto>()

            println("UserLogin: ${githubUserInfo.login}")


            val accessToken = Jwts.builder()
                .header()
                .keyId(kid)
                .and()
                .subject(githubUserInfo.login)
                .audience().add("desktop-client")
                .and()
                .claim("scope", "openid")
                .claim("iss", issuerUrl)
                .claim("exp", System.currentTimeMillis() + (1000 * 60))
                .signWith(keyPair.private)
                .compact()

            val response = TokensRsDto(
                accessToken = accessToken,
                refreshToken = generateNewToken(),
                scope = "scope",
                tokenType = "Bearer",
                expiresIn = System.currentTimeMillis()
            )
            call.respond(response)
        }

}