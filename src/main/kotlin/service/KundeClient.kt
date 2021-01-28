/*
 * Copyright (C) 2021 - present Juergen Zimmermann, Hochschule Karlsruhe
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
package com.acme.gesundheitsamt.service

import com.acme.gesundheitsamt.entity.Kunde
import com.acme.gesundheitsamt.entity.KundeId
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Anwendungslogik für gesundheitsamten.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
class KundeClient(
    // siehe org.springframework.web.reactive.function.client.DefaultWebClientBuilder
    // siehe org.springframework.web.reactive.function.client.DefaultWebClient
    @Lazy private val clientBuilder: WebClient.Builder,
) {
    /**
     * Kunde anhand der Kunde-ID suchen.
     * @param kundeId Die Id des gesuchten Kunden.
     * @return Der gefundene Kunde oder null.
     */
    suspend fun findById(kundeId: KundeId): FindKundeResult {
        logger.debug("findKundeById: kundeId={}, baseUrl={}", kundeId, baseUrl)

        // org.springframework.web.reactive.function.client.DefaultWebClient
        val client = clientBuilder
            .baseUrl(baseUrl)
            .filter(basicAuthentication(username, password))
            .build()

        return try {
            val kunde: Kunde = client
                .get()
                .uri("/api/$kundeId")
                .retrieve()
                .awaitBody()
            logger.debug("findKundeById: {}", kunde)
            FindKundeResult.Success(kunde)
        } catch (e: WebClientResponseException) {
            val classnameFn = { e.javaClass.name }
            logger.error("findKundeById: {}", classnameFn)
            FindKundeResult.Failure(e)
        }
    }

    private companion object {
        // https://github.com/istio/istio/blob/master/samples/bookinfo/src/reviews/reviews-application/src/main/java/application/rest/LibertyRestEndpoint.java#L43
        val kundeService = System.getenv("KUNDE_HOSTNAME") ?: "kunde"
        val kundePort = System.getenv("KUNDE_SERVICE_PORT") ?: "8080"
        val baseUrl = "http://$kundeService:$kundePort"

        const val username = "admin"
        const val password = "p"
        val logger: Logger = LogManager.getLogger(KundeClient::class.java)
    }
}

/**
 * Resultat-Typ für [KundeClient.findById]
 */
sealed class FindKundeResult {
    /**
     * Resultat-Typ, wenn ein Kunde gefunden wurde.
     * @property kunde Der gefundene Kunde
     */
    data class Success(val kunde: Kunde) : FindKundeResult()

    /**
     * Resultat-Typ, wenn bei der Suche nach einem Kunden ein Fehler eingetreten ist.
     * @property exception Die Exception vom Typ WebClientResponseException, z.B. von der abgeleiteten Klasse
     *  WebClientResponseException.NotFound
     */
    data class Failure(val exception: WebClientResponseException) : FindKundeResult()
}
