package ru.kyamshanov.mission.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenIdConfigRsDto(
    @SerialName("issuer")
    val issuer: String,
    @SerialName("authorization_endpoint")
    val authorizationEndpoint: String,
    @SerialName("device_authorization_endpoint")
    val deviceAuthorizationEndpoint: String,
    @SerialName("token_endpoint")
    val tokenEndpoint: String,
    @SerialName("token_endpoint_auth_methods_supported")
    val tokenEndpointAuthMethodsSupported: List<String>,
    @SerialName("jwks_uri")
    val jwksUri: String,
    @SerialName("userinfo_endpoint")
    val userinfoEndpoint: String,
    @SerialName("end_session_endpoint")
    val endSessionEndpoint: String,
    @SerialName("response_types_supported")
    val responseTypesSupported: List<String>,
    @SerialName("grant_types_supported")
    val grantTypesSupported: List<String>,
    @SerialName("revocation_endpoint")
    val revocationEndpoint: String,
    @SerialName("revocation_endpoint_auth_methods_supported")
    val revocationEndpointAuthMethodsSupported: List<String>,
    @SerialName("introspection_endpoint")
    val introspectionEndpoint: String,
    @SerialName("introspection_endpoint_auth_methods_supported")
    val introspectionEndpointAuthMethodsSupported: List<String>,
    @SerialName("subject_types_supported")
    val subjectTypesSupported: List<String>,
    @SerialName("id_token_signing_alg_values_supported")
    val idTokenSigningAlgValuesSupported: List<String>,
    @SerialName("scopes_supported")
    val scopesSupported: List<String>,
)