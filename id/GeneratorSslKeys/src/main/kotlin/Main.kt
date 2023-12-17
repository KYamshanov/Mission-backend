package ru.kyamshanov.local

import io.ktor.network.tls.certificates.*
import java.io.*

fun main() {
    val keyStoreFile = File("GeneratorSslKeys/build/certs/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("idsign") {
            password = "local-idsign-password-HjkrP"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")
}