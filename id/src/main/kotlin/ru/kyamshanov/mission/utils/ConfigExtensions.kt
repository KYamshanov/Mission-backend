package ru.kyamshanov.mission.utils

import io.ktor.server.config.*
import java.nio.file.Files
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.readText


private val secrets: Map<String, Lazy<String>> =
    Files.walk(Path("/run/secrets"))
        .filter { item -> Files.isRegularFile(item) }
        .map { item ->
            val variableName = item.name.also { println("Adding $it secret") }
            val fileValue = lazy { item.readText() }
            variableName to fileValue
        }.collect(Collectors.toMap({ key -> key.first }, { value -> value.second }))

private fun getSecret(secretKey: String): String =
    secrets[secretKey]?.value ?: throw IllegalStateException("Secret has not found by key $secretKey")

/**
 * Support for obtain property using environment or get sensitive data using docker secrets
 */
fun ApplicationConfig.secret(path: String) =
    property(path).getString().let {
        if (it.startsWith("SECRET_")) getSecret(it.removePrefix("SECRET_"))
        else it
    }

private const val SSL_KEY_STORE = "ktor.security.ssl.keyStore"
private const val SSL_KEY_ALIAS = "ktor.security.ssl.keyAlias"
private const val SSL_KEY_STORE_PASSWORD = "ktor.security.ssl.keyStorePassword"
private const val SSL_PRIVATE_KEY_PASSWORD = "ktor.security.ssl.privateKeyPassword"

fun sslSecretsProperies(): List<String> = buildList {
    secrets["ssl_keyStore"]?.let { add("-P:$SSL_KEY_STORE=$it") }
    secrets["ssl_keyAlias"]?.let { add("-P:$SSL_KEY_ALIAS=$it") }
    secrets["ssl_keyStorePassword"]?.let { add("-P:$SSL_KEY_STORE_PASSWORD=$it") }
    secrets["ssl_keyPassword"]?.let { add("-P:$SSL_PRIVATE_KEY_PASSWORD=$it") }
}
