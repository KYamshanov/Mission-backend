package ru.kyamshanov.mission.authorization

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.provisioning.JdbcUserDetailsManager
import ru.kyamshanov.mission.authorization.repositories.AuthorizationConsentRepository
import ru.kyamshanov.mission.authorization.repositories.ClientRepository
import java.util.*
import javax.sql.DataSource


@Configuration
class UserDetailsConfig {

    @Bean
    fun userDetailsService(
        dataSource: DataSource,
        clientRepository: ClientRepository
    ): UserDetailsService {

        println("TEEST: ${clientRepository.findByClientId("desktop-client")}")

        return JdbcUserDetailsManager(dataSource)
    }
}