package ru.kyamshanov.mission.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JwksRsDto(
    @SerialName("keys")
    val keys: List<JwkDto>,
)

/*
{"keys":[{"kty":"RSA","e":"AQAB","kid":"e7f72d81-0911-419f-b761-c07bd6d7fd45","n":"s7izeguRkTEJfFBOo2U-owQI5YQEYIV94vBsU5AaxZNbpGkhg1RrFfQmysEMJQq7hfHa1W3bE57jNJaRCon41gJCOmRJTWP8ZMGyANfx-sn4sUtlLscIUlIlyWOUuNFDGmrDMQoSoCjs7o9OexmOG77RFb4L7XjWJU5Udw_3KgEGvTxjNc0Na-7UAKmkqAdHkaLQGGsrWDO8IDRnsrQAULmfCGk_Tg-1mEy-7gLiglBqM7lvjvbp9BwWJObCiVFAIP1-BE9GUh3046KSHJ0D6B83cYheTeJSIAsLz8vO7fT8Tw41adC6U81YmpBMJTfaarJewpLT_7BZOey3nZuEQQ"}]}
 */

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