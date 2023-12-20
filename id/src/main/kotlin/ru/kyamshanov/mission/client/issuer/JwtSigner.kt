package ru.kyamshanov.mission.client.issuer

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import java.security.KeyPair
import java.util.*

val keyPair: KeyPair = Jwts.SIG.RS256.keyPair().build() //or RS384, RS512, PS256, etc...
val kid = UUID.randomUUID().toString()


/**
 * The Issuer for provide access and refresh tokens
 */
interface JwtSigner {

    fun sign(rawJwt: JwtBuilder): String
}

class SignedJwtSigner(
    private val issuerUrl: String,
) : JwtSigner {
    override fun sign(rawJwt: JwtBuilder): String =
        rawJwt.header()
            .keyId(kid)
            .and()
            .claim("iss", issuerUrl)
            .signWith(keyPair.private)
            .compact()

}