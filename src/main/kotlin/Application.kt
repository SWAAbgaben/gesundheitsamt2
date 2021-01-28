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

package com.acme.gesundheitsamt

import com.acme.gesundheitsamt.config.Settings.banner
import kotlinx.coroutines.InternalCoroutinesApi
import org.springframework.boot.WebApplicationType.REACTIVE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.runApplication

/**
 * Die Klasse, die beim Start des Hauptprogramms verwendet wird, um zu konfigurieren, dass es sich um eine Anwendung mit
 * _Spring Boot_ handelt. Dadurch werden auch viele von Spring Boot gelieferte Konfigurationsklassen automatisch
 * konfiguriert.
 *
 * [Use Cases](../../images/use-cases.svg)
 *
 * [Komponentendiagramm](../../images/komponenten.svg)
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@SpringBootApplication(proxyBeanMethods = false)
class Application

/**
 * Hauptprogramm, um den Microservice zu starten.
 *
 * @param args Evtl. zusätzliche Argumente für den Start des Microservice
 */
@InternalCoroutinesApi
fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<Application>(*args) {
        webApplicationType = REACTIVE
        setBanner(banner)
        @Suppress("MagicNumber")
        applicationStartup = BufferingApplicationStartup(2048)
    }
}
