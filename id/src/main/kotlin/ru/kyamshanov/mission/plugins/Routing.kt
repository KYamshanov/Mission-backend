package ru.kyamshanov.mission.plugins

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Jwks
import io.jsonwebtoken.security.RsaPublicJwk
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.dto.*
import ru.kyamshanov.mission.tables.AuthorizationTable
import ru.kyamshanov.mission.tables.ServiceMetadata
import java.security.KeyPair
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*


val keyPair: KeyPair = Jwts.SIG.RS256.keyPair().build() //or RS384, RS512, PS256, etc...
val kid = UUID.randomUUID().toString()

fun Application.configureRouting(httpClient: HttpClient) {
    routing {
        get("/.well-known/openid-configuration") {
            val issuer = this@configureRouting.environment.config.property("oauth.issuer").getString()
            val response = OpenIdConfigRsDto(
                issuer = issuer,
                authorizationEndpoint = "$issuer/oauth2/authorize",
                deviceAuthorizationEndpoint = "$issuer/oauth2/device_authorization",
                tokenEndpoint = "$issuer/oauth2/device_authorization",
                tokenEndpointAuthMethodsSupported = listOf(
                    "client_secret_basic",
                    "client_secret_post",
                    "client_secret_jwt",
                    "private_key_jwt"
                ),
                jwksUri = "$issuer/oauth2/jwks",
                userinfoEndpoint = "http://localhost:6543/userinfo",
                endSessionEndpoint = "http://localhost:6543/connect/logout",
                responseTypesSupported = listOf("code"),
                grantTypesSupported = listOf(
                    "authorization_code",
                    "client_credentials",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:device_code"
                ),
                revocationEndpoint = "http://localhost:6543/oauth2/revoke",
                revocationEndpointAuthMethodsSupported = listOf(
                    "client_secret_basic",
                    "client_secret_post",
                    "client_secret_jwt",
                    "private_key_jwt"
                ),
                introspectionEndpoint = "http://localhost:6543/oauth2/introspect",
                introspectionEndpointAuthMethodsSupported = listOf(
                    "client_secret_basic",
                    "client_secret_post",
                    "client_secret_jwt",
                    "private_key_jwt"
                ),
                subjectTypesSupported = listOf("public"),
                idTokenSigningAlgValuesSupported = listOf("RS256"),
                scopesSupported = listOf("openid")
            )
            call.respond(response)
        }

        get("/oauth2/jwks") {
            val jwk: RsaPublicJwk = Jwks.builder().rsaKeyPair(keyPair).build().toPublicJwk()
            call.respond(
                JwksRsDto(
                    listOf(
                        JwkDto(
                            jwk["kty"] as String,
                            jwk["e"] as String,
                            kid,
                            jwk["n"] as String,
                        )
                    )
                )
            )
        }

        get("/oauth2/authorize") {
            call.sessions.set(
                OAuthSessionConfig(
                    requireNotNull(call.parameters["code_challenge"]),
                    requireNotNull(call.parameters["scope"]),
                    requireNotNull(call.parameters["redirect_uri"]),
                    requireNotNull(call.parameters["client_id"]),
                )
            )
            call.respondTemplate("login.ftl")
        }

        post("/oauth2/token") {
            val formParameters = call.receiveParameters()
            val authCode: String = requireNotNull(formParameters["code"])
            val codeVerifier: String = requireNotNull(formParameters["code_verifier"])
            val codeChallenge: String
            val token: String
            val scopes: String


            println("authCode")

            transaction {
                AuthorizationTable.select {
                    AuthorizationTable.authCode eq authCode
                }.limit(1).single()
            }.also {
                codeChallenge = it[AuthorizationTable.codeChallenge]
                token = requireNotNull(it[AuthorizationTable.serviceMetadata].token)
                scopes = it[AuthorizationTable.scopes]
            }

            assert(getCodeChallenge(codeVerifier) == codeChallenge)

            val githubUserInfo = httpClient.get("https://api.github.com/user") {
                header("Accept", "application/vnd.github+json")
                header("X-GitHub-Api-Version", "2022-11-28")
                header("Authorization", "Bearer $token")
            }.body<GithubUserRsDto>()

            println("UserLogin: ${githubUserInfo.login}")


            //  val key: SecretKey = keyPair.private. // or RSA or EC PublicKey or PrivateKey
            val issuer = this@configureRouting.environment.config.property("oauth.issuer").getString()

            val accessToken = Jwts.builder()
                .header()
                .keyId(kid)
                .and()
                .subject(githubUserInfo.login)
                .audience().add("desktop-client")
                .and()
                .claim("scope", "openid")
                .claim("iss", issuer)
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


        authenticate("auth-oauth-github") {
            get("/auth/github") {
                println("Regirect")
                // Redirects to 'authorizeUrl' automatically
            }

            get("/github/authorized") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                println("Github cccess token: ${principal?.accessToken}")
                println("State: ${principal?.state}")

                val oAuthConfig = requireNotNull(call.sessions.get<OAuthSessionConfig>())
                call.sessions.clear<OAuthSessionConfig>()

                val authorizationCode = generateNewToken()

                transaction {
                    AuthorizationTable.insert {
                        it[service] = "github"
                        it[authCode] = authorizationCode
                        it[scopes] = oAuthConfig.scopes
                        it[serviceMetadata] = ServiceMetadata(token = principal?.accessToken)
                        it[createdAt] = LocalDateTime.now()
                        it[updatedAt] = LocalDateTime.now()
                        it[codeChallenge] = oAuthConfig.codeChallenge
                        it[enabled] = true
                    }
                }

                call.respondRedirect("${oAuthConfig.callbackURL}?code=${authorizationCode}")
            }
        }
    }
}

private val secureRandom: SecureRandom = SecureRandom()

private val base64Encoder: Base64.Encoder = Base64.getUrlEncoder()


fun generateNewToken(): String {
    val randomBytes = ByteArray(95)
    secureRandom.nextBytes(randomBytes)
    return base64Encoder.encodeToString(randomBytes) //Code length == 128
}

fun getCodeChallenge(codeVerifier: String): String {
    val bytes: ByteArray = codeVerifier.toByteArray(Charsets.US_ASCII)
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes, 0, bytes.size)
    val digest = messageDigest.digest()
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
}