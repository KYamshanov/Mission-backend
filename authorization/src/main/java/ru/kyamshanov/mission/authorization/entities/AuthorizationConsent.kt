package ru.kyamshanov.mission.authorization.entities

import jakarta.persistence.*
import java.io.Serializable
import java.util.*


@Entity
@Table(name = "`authorizationconsent`")
@IdClass(AuthorizationConsent.AuthorizationConsentId::class)
data class AuthorizationConsent(
    @Id
    @Column(name = "registeredclientid")
    var registeredClientId: String? = null,

    @Id
    @Column(name = "principalname")
    var principalName: String? = null,

    @Column(name = "authorities", length = 1000)
    var authorities: String? = null,
) {
    data class AuthorizationConsentId(
        var registeredClientId: String? = null,
        var principalName: String? = null
    ) : Serializable
}