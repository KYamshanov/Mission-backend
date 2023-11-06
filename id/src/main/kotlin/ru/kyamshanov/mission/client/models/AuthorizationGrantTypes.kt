package ru.kyamshanov.mission.client.models

enum class AuthorizationGrantTypes(val stringValue: String) {

    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token")

}