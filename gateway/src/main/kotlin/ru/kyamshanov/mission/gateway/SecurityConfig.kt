package ru.kyamshanov.mission.gateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val s = "{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":{\"@class\":\"java.time.Duration\",\"seconds\":300,\"nano\":0},\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":{\"@class\":\"java.time.Duration\",\"seconds\":3600,\"nano\":0},\"settings.token.authorization-code-time-to-live\":{\"@class\":\"java.time.Duration\",\"seconds\":300,\"nano\":0},\"settings.token.device-code-time-to-live\":{\"@class\":\"java.time.Duration\",\"seconds\":300,\"nano\":0}}"
        return http
            .authorizeExchange { it.anyExchange().authenticated() }
            .oauth2ResourceServer { r -> r.jwt(Customizer.withDefaults()) }
            .csrf(Customizer.withDefaults())
            .build()
    }
}
