/*
 * Copyright (C) 2017 - present Juergen Zimmermann, Hochschule Karlsruhe
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
package com.acme.gesundheitsamt.rest

import am.ik.yavi.core.ConstraintViolation
import com.acme.gesundheitsamt.Router.Companion.idPathVar
import com.acme.gesundheitsamt.entity.gesundheitsamt
import com.acme.gesundheitsamt.entity.gesundheitsamtId
import com.acme.gesundheitsamt.entity.KundeId
import com.acme.gesundheitsamt.service.gesundheitsamtService
import com.acme.gesundheitsamt.service.CreateResult
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.hateoas.server.reactive.toCollectionModelAndAwait
import org.springframework.hateoas.server.reactive.toModelAndAwait
import org.springframework.http.HttpHeaders.IF_NONE_MATCH
import org.springframework.http.HttpStatus.NOT_MODIFIED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.net.URI

/**
 * Eine Handler-Function wird von der Router-Function [com.acme.gesundheitsamt.Router.router] aufgerufen,
 * nimmt einen Request entgegen und erstellt den Response.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 *
 * @constructor Einen gesundheitsamtHandler mit einem injizierten [gesundheitsamtService] erzeugen.
 */
@Component
class GesundheitsamtHandler(private val service: gesundheitsamtService, private val modelAssembler: GesundheitsamtModelAssembler) {
    /**
     * Suche anhand der gesundheitsamt-ID
     * @param request Der eingehende Request
     * @return Ein Response mit dem Statuscode 200 und der gefundenen gesundheitsamt einschließlich Atom-Links,
     *      oder aber Statuscode 204.
     */
    suspend fun findById(request: ServerRequest): ServerResponse {
        val idStr = request.pathVariable(idPathVar)
        val id = GesundheitsamtId.fromString(idStr)

        val gesundheitsamt = service.findById(id) ?: return notFound().buildAndAwait()
        logger.debug("findById: {}", gesundheitsamt)

        val version = gesundheitsamt.version
        val versionHeader = request.headers()
            .header(IF_NONE_MATCH)
            .firstOrNull()
            ?.toIntOrNull()

        if (version == versionHeader) {
            return status(NOT_MODIFIED).buildAndAwait()
        }

        val gesundheitsamtModel = modelAssembler.toModelAndAwait(gesundheitsamt, request.exchange())
        // Entity Tag, um Aenderungen an der angeforderten Ressource erkennen zu koennen.
        // Client: GET-Requests mit Header "If-None-Match"
        //         ggf. Response mit Statuscode NOT MODIFIED (s.o.)
        return ok().eTag("\"$version\"").bodyValueAndAwait(gesundheitsamtModel)
    }

    /**
     * Suche mit diversen Suchkriterien als Query-Parameter. Es wird eine Liste zurückgeliefert, damit auch der
     * Statuscode 204 möglich ist.
     * @param request Der eingehende Request mit den Query-Parametern.
     * @return Ein Response mit dem Statuscode 200 und einer Liste mit den gefundenen gesundheitsamten einschließlich
     *      Atom-Links, oder aber Statuscode 204.
     */
    @Suppress("ReturnCount", "LongMethod")
    suspend fun find(request: ServerRequest): ServerResponse {
        val queryParams = request.queryParams()
        if (queryParams.size > 1) {
            return notFound().buildAndAwait()
        }

        val gesundheitsamten = if (queryParams.isEmpty()) {
            service.findAll()
        } else {
            val kundeIdStr = queryParams["kundeId"]?.first()
                ?: return notFound().buildAndAwait()

            val kundeId = KundeId.fromString(kundeIdStr)
            service.findByKundeId(kundeId)
        }

        val gesundheitsamtenList = mutableListOf<Gesundheitsamt>()
        gesundheitsamten.toList(gesundheitsamtenList)

        return if (gesundheitsamtenList.isEmpty()) {
            notFound().buildAndAwait()
        } else {
            val gesundheitsamtenModel =
                modelAssembler.toCollectionModelAndAwait(gesundheitsamtenList.asFlow(), request.exchange())
            logger.debug("find: {}", gesundheitsamtenModel)
            ok().bodyValueAndAwait(gesundheitsamtenModel)
        }
    }

    /**
     * Einen neuen gesundheitsamt-Datensatz anlegen.
     * @param request Der eingehende Request mit dem gesundheitsamt-Datensatz im Body.
     * @return Response mit Statuscode 201 einschließlich Location-Header oder Statuscode 400 falls Constraints verletzt
     *      sind oder der JSON-Datensatz syntaktisch nicht korrekt ist.
     */
    suspend fun create(request: ServerRequest): ServerResponse {
        val gesundheitsamt = request.awaitBody<gesundheitsamt>()

        return when (val result = service.create(gesundheitsamt)) {
            is CreateResult.Success -> handleCreated(result.gesundheitsamt, request)
            is CreateResult.ConstraintViolations -> handleConstraintViolations(result.violations)
        }
    }

    private suspend fun handleCreated(gesundheitsamt: gesundheitsamt, request: ServerRequest): ServerResponse {
        logger.debug("handleCreated: {}", gesundheitsamt)
        val baseUri = getBaseUri(request.headers().asHttpHeaders(), request.uri())
        val location = URI("$baseUri/${gesundheitsamt.id}")
        logger.debug("handleCreated: {}", location)
        return created(location).buildAndAwait()
    }

    // z.B. Service-Funktion "create|update" mit Parameter "gesundheitsamt" hat dann
    // Meldungen mit "create.gesundheitsamt.nachname:"
    private suspend fun handleConstraintViolations(violations: Collection<ConstraintViolation>): ServerResponse {
        if (violations.isEmpty()) {
            return ServerResponse.badRequest().buildAndAwait()
        }

        val gesundheitsamtViolations = violations.associate { violation ->
            violation.messageKey() to violation.message()
        }
        logger.debug("handleConstraintViolations(): {}", gesundheitsamtViolations)

        return ServerResponse.badRequest().bodyValueAndAwait(gesundheitsamtViolations)
    }

    private companion object {
        val logger: Logger = LogManager.getLogger(gesundheitsamtHandler::class.java)
    }
}
