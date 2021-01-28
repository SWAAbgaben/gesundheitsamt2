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
package com.acme.gesundheitsamt.service

import am.ik.yavi.core.ConstraintViolation
import com.acme.gesundheitsamt.entity.Gesundheitsamt
import com.acme.gesundheitsamt.entity.GesundheitsamtId
import com.acme.gesundheitsamt.entity.Kunde
import com.acme.gesundheitsamt.entity.KundeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations
import org.springframework.data.mongodb.core.awaitOneOrNull
import org.springframework.data.mongodb.core.flow
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.query
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Anwendungslogik für gesundheitsamten.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
class GesundheitsamtService(
    private val validator: GesundheitsamtValidator,
    private val mongo: ReactiveFluentMongoOperations,
    // siehe org.springframework.web.reactive.function.client.DefaultWebClientBuilder
    // siehe org.springframework.web.reactive.function.client.DefaultWebClient
    @Lazy private val kundeClient: KundeClient,
) {
    /**
     * Alle gesundheitsamten ermitteln.
     * @return Alle gesundheitsamten.
     */
    suspend fun findAll(): Flow<Gesundheitsamt> = mongo.query<Gesundheitsamt>()
        .flow()
        .onEach { gesundheitsamt ->
            logger.debug("findAll: {}", gesundheitsamt)
            val (nachname) = findKundeById(gesundheitsamt.kundeId)
            gesundheitsamt.kundeNachname = nachname
        }

    /**
     * Eine gesundheitsamt anhand der ID suchen.
     * @param id Die Id der gesuchten gesundheitsamt.
     * @return Die gefundene gesundheitsamt oder null.
     */
    suspend fun findById(id: GesundheitsamtId): Gesundheitsamt? {
        val gesundheitsamt = mongo.query<Gesundheitsamt>()
            .matching(query(Gesundheitsamt::id isEqualTo id))
            .awaitOneOrNull()
        logger.debug("findById: {}", gesundheitsamt)
        if (gesundheitsamt == null) {
            return gesundheitsamt
        }

        // Destructuring
        val (nachname) = findKundeById(gesundheitsamt.kundeId)
        return gesundheitsamt.apply { kundeNachname = nachname }
    }

    /**
     * gesundheitsamten zur Kunde-ID suchen.
     * @param kundeId Die Id des gegebenen Kunden.
     * @return Die gefundenen gesundheitsamten oder ein leeres Flux-Objekt.
     */
    suspend fun findByKundeId(kundeId: KundeId): Flow<Gesundheitsamt> {
        val (nachname) = findKundeById(kundeId)

        val criteria = where(Gesundheitsamt::kundeId).regex("\\.*$kundeId\\.*", "i")
        return mongo.query<Gesundheitsamt>().matching(Query(criteria))
            .flow()
            .onEach { gesundheitsamt ->
                logger.debug("findByKundeId: {}", gesundheitsamt)
                gesundheitsamt.kundeNachname = nachname
            }
    }

    private suspend fun findKundeById(kundeId: KundeId): Kunde {
        return when (val result = kundeClient.findById(kundeId)) {
            is FindKundeResult.Success -> result.kunde
            is FindKundeResult.Failure -> {
                if (result is WebClientResponseException.NotFound) {
                    logger.debug("findKundeById: WebClientResponseException.NotFound")
                    Kunde(nachname = "Notfound", email = "notfound@acme.com")
                } else {
                    // Unauthorized (401), Forbidden (403), ...
                    Kunde(nachname = "Otherexception", email = "otherexception@acme.com")
                }
            }
        }
    }

    /**
     * Eine neue gesundheitsamt anlegen.
     * @param gesundheitsamt Das Objekt der neu anzulegenden gesundheitsamt.
     * @return Die neu angelegte gesundheitsamt mit generierter ID.
     */
    suspend fun create(gesundheitsamt: Gesundheitsamt): CreateResult {
        logger.debug("create: {}", gesundheitsamt)
        val violations = validator.validate(gesundheitsamt)
        if (violations.isNotEmpty()) {
            return CreateResult.ConstraintViolations(violations)
        }

        val neuesGesundheitsamt = mongo.insert<Gesundheitsamt>().oneAndAwait(gesundheitsamt)
        return CreateResult.Success(neuesGesundheitsamt)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(GesundheitsamtService::class.java)
    }
}

/**
 * Resultat-Typ für [gesundheitsamtService.create]
 */
sealed class CreateResult {
    /**
     * Resultat-Typ, wenn eine neue gesundheitsamt erfolgreich angelegt wurde.
     * @property gesundheitsamt Die neu angelegte gesundheitsamt
     */
    data class Success(val gesundheitsamt: Gesundheitsamt) : CreateResult()

    /**
     * Resultat-Typ, wenn eine gesundheitsamt wegen Constraint-Verletzungen nicht angelegt wurde.
     * @property violations Die verletzten Constraints
     */
    data class ConstraintViolations(val violations: Collection<ConstraintViolation>) : CreateResult()
}
