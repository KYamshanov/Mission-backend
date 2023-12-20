package ru.kyamshanov.mission.client.issuer

import java.security.SecureRandom
import java.util.*

interface TokenIssuer {

    fun generateToken(): String
}


class SecureRandomTokenIssuer(
    private val secureRandom: SecureRandom = SecureRandom()
) : TokenIssuer {


    private val base64Encoder: Base64.Encoder = Base64.getUrlEncoder()

    override fun generateToken(): String {
        val randomBytes = ByteArray(95)
        secureRandom.nextBytes(randomBytes)
        return base64Encoder.encodeToString(randomBytes) //Code length == 128
    }

}