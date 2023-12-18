package ru.kyamshanov.mission.security

import java.security.KeyStore
import javax.crypto.Cipher

interface SimpleCipher {

    fun encrypt(rawString: String): String


    fun decrypt(encodedString: String): String
}

class SymmetricCipher(
    private val keyStore: KeyStore,
    private val keyAlias: String,
    private val keyPassword: CharArray
) : SimpleCipher {

    private val cipherEncryptMode = Cipher.getInstance(TRANSFORMATION_KEY)
        .apply { init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyAlias, keyPassword)) }

    private val cipherDecryptMode = Cipher.getInstance(TRANSFORMATION_KEY)
        .apply { init(Cipher.DECRYPT_MODE, keyStore.getKey(keyAlias, keyPassword)) }


    override fun encrypt(rawString: String): String =
        cipherEncryptMode.doFinal(rawString.encodeToByteArray())
            .let { java.util.Base64.getEncoder().encodeToString(it) }


    override fun decrypt(encodedString: String): String =
        java.util.Base64.getDecoder().decode(encodedString)
            .let { cipherDecryptMode.doFinal(it) }
            .let { String(it) }


    companion object {

        private const val TRANSFORMATION_KEY: String = "AES/ECB/PKCS5Padding"
    }
}
