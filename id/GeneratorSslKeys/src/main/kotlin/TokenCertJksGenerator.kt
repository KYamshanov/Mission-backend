package ru.kyamshanov.local

import io.ktor.network.tls.certificates.*
import java.io.File
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


fun main() {
    val keyStorePassword = "123456"

    val keyStoreFile = File("GeneratorSslKeys/build/certs/tokenkeystore.jks")
    val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, keyStorePassword.toCharArray())

    val keygen: SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()

    keyStore.setEntry(
        "token-sign",
        KeyStore.SecretKeyEntry(keygen),
        KeyStore.PasswordProtection("local-idsign-password-HjkrP".toCharArray())
    )

    keyStore.saveToFile(keyStoreFile, keyStorePassword)
}