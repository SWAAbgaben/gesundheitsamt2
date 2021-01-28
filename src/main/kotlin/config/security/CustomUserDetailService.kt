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

import com.acme.gesundheitsamt.config.security.Rolle.actuator
import com.acme.gesundheitsamt.config.security.Rolle.admin
import com.acme.gesundheitsamt.config.security.Rolle.kunde
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User

interface CustomUserDetailService {
    /**
     * Bean-Definition, um den Administrations-User im Hauptspeicher bereitzustellen.
     *
     * @return Objekt von `MapReactiveUserDetailsService`
     */
    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        @Suppress("DEPRECATION")
        val admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("p")
            .roles(admin, kunde, actuator)
            .build()
        return MapReactiveUserDetailsService(admin)
    }
}
