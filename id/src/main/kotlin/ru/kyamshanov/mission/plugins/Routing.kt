package ru.kyamshanov.mission.plugins

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Jwks
import io.jsonwebtoken.security.RsaPublicJwk
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.kyamshanov.mission.authorization.Auth
import ru.kyamshanov.mission.client.models.SocialService
import ru.kyamshanov.mission.dto.JwkDto
import ru.kyamshanov.mission.dto.JwksRsDto
import ru.kyamshanov.mission.dto.OpenIdConfigRsDto
import java.security.KeyPair
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*


val keyPair: KeyPair = Jwts.SIG.RS256.keyPair().build() //or RS384, RS512, PS256, etc...
val kid = UUID.randomUUID().toString()

fun Application.configureRouting(httpClient: HttpClient, auth: Auth) {
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
            auth.authorize(this)
        }

        post("/oauth2/token") {
            auth.token(this)
        }


        authenticate("auth-oauth-github") {

            post("/auth/github") { }

            get("/github/authorized") { auth.authorizedBy(SocialService.GITHUB, this) }
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