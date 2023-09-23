package ru.kyamshanov.mission.gateway


import kotlinx.coroutines.reactor.mono
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.reactive.server.awaitPrincipal
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
internal class AuthorizationFilter(
) : GlobalFilter {


    override fun filter(serverWebExchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> =
        mono {
            val requestBuilder = serverWebExchange.request.mutate()
            val par = serverWebExchange.awaitPrincipal<JwtAuthenticationToken>()
            requestBuilder.appendHeader(USER_ID_HEADER_KEY, par.name)
            requestBuilder
        }.flatMap { requestBuilder ->
            chain.filter(serverWebExchange.mutate().request(requestBuilder.build()).build())
        }

    private fun ServerHttpRequest.Builder.appendHeader(key: String, value: String?) = header(key, value)

    private companion object {
        const val USER_ID_HEADER_KEY = "user-id"
    }
}