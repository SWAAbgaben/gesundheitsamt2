/*
 * Copyright (C) 2016 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.gesundheitsamt.config.security

import com.acme.gesundheitsamt.Router.Companion.apiPath
import com.acme.gesundheitsamt.config.security.Rolle.actuator
import com.acme.gesundheitsamt.config.security.Rolle.admin
import com.acme.gesundheitsamt.config.security.Rolle.kunde
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers

/**
 * Security-Konfiguration.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
// https://github.com/spring-projects/spring-security/tree/master/samples
interface AuthorizationConfig {
    /**
     * Bean-Definition, um den Zugriffsschutz an der REST-Schnittstelle zu konfigurieren.
     *
     * @param http Injiziertes Objekt von `ServerHttpSecurity` als Ausgangspunkt für die Konfiguration.
     * @return Objekt von `SecurityWebFilterChain`.
     */
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        authorizeExchange {
            val gesundheitsamtIdPath = "$apiPath/*"

            authorize(pathMatchers(POST, apiPath), hasRole(kunde))
            authorize(pathMatchers(GET, apiPath, gesundheitsamtIdPath), hasRole(admin))
            authorize(pathMatchers(GET, apiPath), hasRole(admin))

            authorize(EndpointRequest.to(HealthEndpoint::class.java), permitAll)
            authorize(EndpointRequest.toAnyEndpoint(), hasRole(actuator))
        }

        httpBasic {}
        formLogin { disable() }
        headers { contentSecurityPolicy { policyDirectives = "default-src 'self'" } }
        csrf { disable() }
    }
}
