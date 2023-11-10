package ru.kyamshanov.mission.client.delegates

import io.jsonwebtoken.Jwts
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.client.ClientFactory
import ru.kyamshanov.mission.client.TokenDelegate
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.TokensRsDto
import ru.kyamshanov.mission.identification.IdentificationService
import ru.kyamshanov.mission.identification.IdentificationServiceFactory
import ru.kyamshanov.mission.plugins.*
import ru.kyamshanov.mission.tables.AuthorizationTable

class AuthorizationCodeTokenDelegate(
    private val issuerUrl: String,
    private val clientFactory: ClientFactory,
    private val formParameters: Parameters
) : TokenDelegate {
    override suspend fun execute(pipeline: PipelineContext<Unit, ApplicationCall>, httpClient: HttpClient) =
        pipeline.run {
            val authCode: String = requireNotNull(formParameters["code"])
            val codeVerifier: String = requireNotNull(formParameters["code_verifier"])
            val clientId: String
            val codeChallenge: String
            val token: String
            val scopes: String
            val socialService: SocialService


            println("authCode")

            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.authenticationCode eq authCode
                }.limit(1).single()
            }.also {
                clientId = it[AuthorizationTable.clientId]
                codeChallenge = it[AuthorizationTable.authorizationMetadata].codeChallenge
                token = requireNotNull(it[AuthorizationTable.authorizationMetadata].token)
                scopes = it[AuthorizationTable.scopes]
                socialService = it[AuthorizationTable.socialService]
            }

            assert(getCodeChallenge(codeVerifier) == codeChallenge)

            val userId =
                clientFactory.create(clientId).getOrThrow().identificationServices[socialService]!!.identify(token)


            val accessToken = Jwts.builder()
                .header()
                .keyId(kid)
                .and()
                .subject(userId)
                .audience().add("desktop-client")
                .and()
                .claim("scope", scopes)
                .claim("iss", issuerUrl)
                .claim("exp", System.currentTimeMillis() + (1000 * 60 * 10)) // configure exp time
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