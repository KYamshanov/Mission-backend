package ru.kyamshanov.mission.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JwksRsDto(
    @SerialName("keys")
    val keys: List<JwkDto>,
)

@Serializable
data class JwkDto(
    @SerialName("kty")
    val kty: String,
    @SerialName("e")
    val e: String,
    @SerialName("kid")
    val kid: String,
    @SerialName("n")
    val n: String,
)