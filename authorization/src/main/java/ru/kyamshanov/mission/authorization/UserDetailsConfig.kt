package ru.kyamshanov.mission.authorization

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.JdbcUserDetailsManager
import ru.kyamshanov.mission.authorization.repositories.ClientRepository
import javax.sql.DataSource


@Configuration
class UserDetailsConfig {

    @Bean
    fun userDetailsService(
        dataSource: DataSource,
        clientRepository: ClientRepository
    ): UserDetailsService = JdbcUserDetailsManager(dataSource)
}