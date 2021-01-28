/*
 * Copyright (C) 2018 - present Juergen Zimmermann, Hochschule Karlsruhe
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
package com.acme.gesundheitsamt.config.dev

import com.acme.gesundheitsamt.config.Settings.DEV
import com.acme.gesundheitsamt.entity.KundeId
import com.acme.gesundheitsamt.service.KundeClient
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

/**
 * Den Microservice _kunde_ mit WebClient aufrufen.
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
interface LogWebClientKunde {
    /**
     * Bean-Definition, um einen CommandLineRunner für das Profil "dev" bereitzustellen, damit der Microservice _kunde_
     * mit WebClient aufgerufen wird.
     * @param kundeClient Client für den Microservice _kunde_
     * @return CommandLineRunner
     */
    @Bean
    @Profile(DEV)
    @Suppress("LongMethod")
    fun logWebClientKunde(kundeClient: KundeClient) = CommandLineRunner {
        val logger: Logger = LogManager.getLogger(DbPopulate::class.java)

        runBlocking {
            val kundeId = KundeId.fromString("00000000-0000-0000-0000-000000000001")
            val result = kundeClient.findById(kundeId)
            logger.warn("Resultat zur ID {}: {}", kundeId, result)

            val kundeIdNichtvorhanden = KundeId.fromString("ffffffff-0000-0000-0000-999999999999")
            val resultNichtvorhanden = kundeClient.findById(kundeIdNichtvorhanden)
            logger.warn("Resultat zur ID {}: {}", kundeIdNichtvorhanden, resultNichtvorhanden)
        }
    }

    // Fuer OAuth siehe
    // https://github.com/bclozel/spring-reactive-university/blob/master/src/main/java/com/example/integration/...
    //      ...gitter/GitterClient.java
}
