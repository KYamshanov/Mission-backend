package ru.kyamshanov.mission.gateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange {
                it
                    .pathMatchers("/oauth2/**").permitAll()
                    .pathMatchers("/point/private/attached").hasAnyAuthority("SCOPE_openid", "SCOPE_openid:edit")
                    .anyExchange().denyAll()

            }
            .csrf { it.disable() }
            .oauth2ResourceServer { r -> r.jwt(Customizer.withDefaults()) }
            .build()
    }
}
