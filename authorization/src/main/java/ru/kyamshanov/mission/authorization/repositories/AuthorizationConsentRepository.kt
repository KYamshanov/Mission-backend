package ru.kyamshanov.mission.authorization.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authorization.entities.AuthorizationConsent
import ru.kyamshanov.mission.authorization.entities.AuthorizationConsent.AuthorizationConsentId
import java.util.*


@Repository
interface AuthorizationConsentRepository : JpaRepository<AuthorizationConsent, AuthorizationConsentId> {
    fun findByRegisteredClientIdAndPrincipalName(
        registeredClientId: String,
        principalName: String
    ): Optional<AuthorizationConsent>

    fun deleteByRegisteredClientIdAndPrincipalName(registeredClientId: String, principalName: String)
}